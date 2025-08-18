-- =========================================
-- TaskFlow Database Schema - Optimized
-- =========================================

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =========================================
-- Common Functions and Triggers
-- =========================================

-- Function to auto-update updated_at timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Function to validate emailEntity format
CREATE OR REPLACE FUNCTION validate_email(email_input TEXT)
    RETURNS BOOLEAN AS
$$
BEGIN
    RETURN email_input ~* '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$';
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- Function to generate access code
CREATE OR REPLACE FUNCTION generate_access_code()
    RETURNS TEXT AS
$$
DECLARE
    chars TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    result TEXT := '';
    i INTEGER;
BEGIN
    FOR i IN 1..8 LOOP
        result := result || substr(chars, floor(random() * length(chars) + 1)::integer, 1);
    END LOOP;
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- Function to check team membership and role
CREATE OR REPLACE FUNCTION check_team_access(user_uuid UUID, team_uuid UUID, required_role TEXT DEFAULT 'USER')
    RETURNS BOOLEAN AS
$$
DECLARE
    user_role TEXT;
BEGIN
    SELECT role INTO user_role
    FROM team_memberships
    WHERE user_id = user_uuid AND team_id = team_uuid;

    IF user_role IS NULL THEN
        RETURN FALSE;
    END IF;

    -- Role hierarchy check
    CASE required_role
        WHEN 'USER' THEN RETURN user_role IN ('USER', 'MANAGER', 'ADMIN');
        WHEN 'MANAGER' THEN RETURN user_role IN ('MANAGER', 'ADMIN');
        WHEN 'ADMIN' THEN RETURN user_role = 'ADMIN';
        ELSE RETURN FALSE;
    END CASE;
END;
$$ LANGUAGE plpgsql STABLE;

-- =========================================
-- Tables Creation
-- =========================================

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    emailEntity VARCHAR(255) NOT NULL UNIQUE,
    usernameEntity VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    telegram_id VARCHAR(255) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT users_email_format CHECK (validate_email(emailEntity)),
    CONSTRAINT users_username_format CHECK (usernameEntity ~* '^[a-zA-Z0-9_-]+$'),
    CONSTRAINT users_username_length CHECK (LENGTH(usernameEntity) BETWEEN 3 AND 50),
    CONSTRAINT users_password_length CHECK (LENGTH(password) >= 6)
);

-- Teams table
CREATE TABLE teams (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    access_code VARCHAR(8) UNIQUE NOT NULL DEFAULT generate_access_code(),
    access_password VARCHAR(255) NOT NULL,
    created_by UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT teams_name_length CHECK (LENGTH(TRIM(name)) BETWEEN 1 AND 100),
    CONSTRAINT teams_access_code_format CHECK (access_code ~* '^[A-Z0-9]{8}$'),
    CONSTRAINT teams_access_password_length CHECK (LENGTH(access_password) >= 6),

    -- Foreign Keys
    CONSTRAINT fk_teams_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- Team memberships (Many-to-Many relationship)
CREATE TABLE team_memberships (
    team_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(10) NOT NULL DEFAULT 'USER',
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (team_id, user_id),

    -- Constraints
    CONSTRAINT team_memberships_role_check CHECK (role IN ('USER', 'MANAGER', 'ADMIN')),

    -- Foreign Keys
    CONSTRAINT fk_team_memberships_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_team_memberships_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Projects table
CREATE TABLE projects (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    team_id UUID NOT NULL,
    created_by UUID NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT projects_name_length CHECK (LENGTH(TRIM(name)) BETWEEN 1 AND 200),

    -- Foreign Keys
    CONSTRAINT fk_projects_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE,
    CONSTRAINT fk_projects_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- Categories table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    team_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT categories_name_length CHECK (LENGTH(TRIM(name)) BETWEEN 1 AND 100),

    -- Unique constraint for team scoped categories
    CONSTRAINT uq_categories_team_name UNIQUE (team_id, name),

    -- Foreign Keys
    CONSTRAINT fk_categories_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

-- Tags table
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL,
    team_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT tags_name_length CHECK (LENGTH(TRIM(name)) BETWEEN 1 AND 50),

    -- Unique constraint for team scoped tags
    CONSTRAINT uq_tags_team_name UNIQUE (team_id, name),

    -- Foreign Keys
    CONSTRAINT fk_tags_team FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

-- Tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(12) NOT NULL DEFAULT 'NEW',
    priority VARCHAR(6) NOT NULL DEFAULT 'MEDIUM',
    deadline TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    category_id UUID,
    assigned_to UUID,
    project_id UUID NOT NULL,
    created_by UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT tasks_title_length CHECK (LENGTH(TRIM(title)) BETWEEN 1 AND 200),
    CONSTRAINT tasks_status_check CHECK (status IN ('NEW', 'IN_PROGRESS', 'REVIEW', 'DONE', 'CANCELLED')),
    CONSTRAINT tasks_priority_check CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT')),
    CONSTRAINT tasks_completion_logic CHECK (
        (status = 'DONE' AND completed_at IS NOT NULL) OR
        (status != 'DONE' AND completed_at IS NULL)
    ),

    -- Foreign Keys
    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE RESTRICT
);

-- Task-Tags relationship (Many-to-Many)
CREATE TABLE task_tags (
    task_id UUID NOT NULL,
    tag_id UUID NOT NULL,

    PRIMARY KEY (task_id, tag_id),

    -- Foreign Keys
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Comments table
CREATE TABLE comments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content TEXT NOT NULL,
    author_id UUID NOT NULL,
    task_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT comments_content_length CHECK (LENGTH(TRIM(content)) >= 1),

    -- Foreign Keys
    CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comments_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

-- =========================================
-- Triggers for automatic timestamp updates
-- =========================================

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_teams_updated_at
    BEFORE UPDATE ON teams
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_projects_updated_at
    BEFORE UPDATE ON projects
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_categories_updated_at
    BEFORE UPDATE ON categories
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_tags_updated_at
    BEFORE UPDATE ON tags
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_tasks_updated_at
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER trigger_comments_updated_at
    BEFORE UPDATE ON comments
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- =========================================
-- Business Logic Triggers
-- =========================================

-- Trigger to auto-set completed_at when task status changes to DONE
CREATE OR REPLACE FUNCTION auto_complete_task()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.status = 'DONE' AND OLD.status != 'DONE' THEN
        NEW.completed_at = CURRENT_TIMESTAMP;
    ELSIF NEW.status != 'DONE' AND OLD.status = 'DONE' THEN
        NEW.completed_at = NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_task_completion
    BEFORE UPDATE ON tasks
    FOR EACH ROW
    EXECUTE FUNCTION auto_complete_task();

-- Trigger to ensure team creator becomes admin
CREATE OR REPLACE FUNCTION ensure_team_creator_admin()
    RETURNS TRIGGER AS
$$
BEGIN
    INSERT INTO team_memberships (team_id, user_id, role)
    VALUES (NEW.id, NEW.created_by, 'ADMIN')
    ON CONFLICT (team_id, user_id) DO UPDATE SET role = 'ADMIN';

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_team_creator_admin
    AFTER INSERT ON teams
    FOR EACH ROW
    EXECUTE FUNCTION ensure_team_creator_admin();

-- =========================================
-- Performance Indexes
-- =========================================

-- Users indexes
CREATE INDEX idx_users_email ON users(emailEntity);
CREATE INDEX idx_users_username ON users(usernameEntity);
CREATE INDEX idx_users_active ON users(active) WHERE active = true;
CREATE INDEX idx_users_telegram_id ON users(telegram_id) WHERE telegram_id IS NOT NULL;

-- Teams indexes
CREATE INDEX idx_teams_created_by ON teams(created_by);
CREATE INDEX idx_teams_active ON teams(active) WHERE active = true;
CREATE INDEX idx_teams_access_code ON teams(access_code);
CREATE INDEX idx_teams_name_trgm ON teams USING gin(name gin_trgm_ops); -- For full-text search

-- Team memberships indexes
CREATE INDEX idx_team_memberships_user_id ON team_memberships(user_id);
CREATE INDEX idx_team_memberships_role ON team_memberships(role);

-- Projects indexes
CREATE INDEX idx_projects_team_id ON projects(team_id);
CREATE INDEX idx_projects_created_by ON projects(created_by);
CREATE INDEX idx_projects_active ON projects(active) WHERE active = true;
CREATE INDEX idx_projects_name_trgm ON projects USING gin(name gin_trgm_ops);

-- Categories indexes
CREATE INDEX idx_categories_team_id ON categories(team_id);
CREATE INDEX idx_categories_name ON categories(name);

-- Tags indexes
CREATE INDEX idx_tags_team_id ON tags(team_id);
CREATE INDEX idx_tags_name ON tags(name);

-- Tasks indexes - Most critical for performance
CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to) WHERE assigned_to IS NOT NULL;
CREATE INDEX idx_tasks_created_by ON tasks(created_by);
CREATE INDEX idx_tasks_category_id ON tasks(category_id) WHERE category_id IS NOT NULL;
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_deadline ON tasks(deadline) WHERE deadline IS NOT NULL;
CREATE INDEX idx_tasks_created_at ON tasks(created_at);
CREATE INDEX idx_tasks_updated_at ON tasks(updated_at);

-- Compound indexes for common queries
CREATE INDEX idx_tasks_project_status ON tasks(project_id, status);
CREATE INDEX idx_tasks_assigned_status ON tasks(assigned_to, status) WHERE assigned_to IS NOT NULL;
CREATE INDEX idx_tasks_project_assigned ON tasks(project_id, assigned_to) WHERE assigned_to IS NOT NULL;
CREATE INDEX idx_tasks_deadline_status ON tasks(deadline, status) WHERE deadline IS NOT NULL;

-- Task tags indexes
CREATE INDEX idx_task_tags_tag_id ON task_tags(tag_id);

-- Comments indexes
CREATE INDEX idx_comments_task_id ON comments(task_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);
CREATE INDEX idx_comments_created_at ON comments(created_at);

-- =========================================
-- Views for common queries
-- =========================================

-- View for active user tasks with project and team info
CREATE OR REPLACE VIEW user_active_tasks AS
SELECT
    t.id,
    t.title,
    t.description,
    t.status,
    t.priority,
    t.deadline,
    t.assigned_to,
    t.created_by,
    t.created_at,
    t.updated_at,
    p.name as project_name,
    p.team_id,
    tm.name as team_name,
    c.name as category_name,
    ARRAY_AGG(DISTINCT tg.name) FILTER (WHERE tg.name IS NOT NULL) as tag_names
FROM tasks t
JOIN projects p ON t.project_id = p.id
JOIN teams tm ON p.team_id = tm.id
LEFT JOIN categories c ON t.category_id = c.id
LEFT JOIN task_tags tt ON t.id = tt.task_id
LEFT JOIN tags tg ON tt.tag_id = tg.id
WHERE t.status NOT IN ('DONE', 'CANCELLED')
  AND p.active = true
  AND tm.active = true
GROUP BY t.id, t.title, t.description, t.status, t.priority, t.deadline,
         t.assigned_to, t.created_by, t.created_at, t.updated_at,
         p.name, p.team_id, tm.name, c.name;

-- View for team statistics
CREATE OR REPLACE VIEW team_statistics AS
SELECT
    t.id as team_id,
    t.name as team_name,
    COUNT(DISTINCT tm.user_id) as member_count,
    COUNT(DISTINCT p.id) as project_count,
    COUNT(DISTINCT CASE WHEN p.active THEN p.id END) as active_project_count,
    COUNT(DISTINCT tk.id) as task_count,
    COUNT(DISTINCT CASE WHEN tk.status = 'DONE' THEN tk.id END) as completed_task_count,
    COUNT(DISTINCT CASE WHEN tk.status IN ('NEW', 'IN_PROGRESS', 'REVIEW') THEN tk.id END) as active_task_count
FROM teams t
LEFT JOIN team_memberships tm ON t.id = tm.team_id
LEFT JOIN projects p ON t.id = p.team_id
LEFT JOIN tasks tk ON p.id = tk.project_id
WHERE t.active = true
GROUP BY t.id, t.name;

-- =========================================
-- Security Functions
-- =========================================

-- Row Level Security for teams (example)
ALTER TABLE teams ENABLE ROW LEVEL SECURITY;

-- Policy to allow users to see only teams they are members of
CREATE POLICY team_member_policy ON teams
    FOR ALL
    TO authenticated_user
    USING (
        id IN (
            SELECT team_id
            FROM team_memberships
            WHERE user_id = current_setting('app.current_user_id')::UUID
        )
    );

-- =========================================
-- Maintenance Functions
-- =========================================

-- Function to cleanup old completed tasks (archive)
CREATE OR REPLACE FUNCTION archive_old_completed_tasks(days_old INTEGER DEFAULT 365)
    RETURNS INTEGER AS
$$
DECLARE
    archived_count INTEGER;
BEGIN
    WITH archived_tasks AS (
        DELETE FROM tasks
        WHERE status = 'DONE'
          AND completed_at < CURRENT_DATE - INTERVAL '1 day' * days_old
        RETURNING *
    )
    SELECT COUNT(*) INTO archived_count FROM archived_tasks;

    RETURN archived_count;
END;
$$ LANGUAGE plpgsql;

-- Function to generate database statistics
CREATE OR REPLACE FUNCTION get_database_stats()
    RETURNS TABLE (
        table_name TEXT,
        row_count BIGINT,
        table_size TEXT
    ) AS
$$
BEGIN
    RETURN QUERY
    SELECT
        schemaname||'.'||tablename as table_name,
        n_tup_ins - n_tup_del as row_count,
        pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as table_size
    FROM pg_stat_user_tables
    WHERE schemaname = 'public'
    ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;
END;
$$ LANGUAGE plpgsql;

-- =========================================
-- Enable extensions for better performance
-- =========================================

-- Enable pg_trgm for better text search performance
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- Enable pg_stat_statements for query performance monitoring
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- =========================================
-- Comments for documentation
-- =========================================

COMMENT ON TABLE users IS 'Application users with authentication data';
COMMENT ON TABLE teams IS 'Teams that organize projects and users';
COMMENT ON TABLE team_memberships IS 'Many-to-many relationship between teams and users with roles';
COMMENT ON TABLE projects IS 'Projects belonging to teams';
COMMENT ON TABLE categories IS 'Task categories scoped to teams';
COMMENT ON TABLE tags IS 'Task tags scoped to teams';
COMMENT ON TABLE tasks IS 'Individual tasks within projects';
COMMENT ON TABLE task_tags IS 'Many-to-many relationship between tasks and tags';
COMMENT ON TABLE comments IS 'Comments on tasks';

COMMENT ON FUNCTION update_timestamp() IS 'Trigger function to automatically update updated_at timestamp';
COMMENT ON FUNCTION check_team_access(UUID, UUID, TEXT) IS 'Check if user has required access level to team';
COMMENT ON FUNCTION generate_access_code() IS 'Generate random 8-character access code for teams';