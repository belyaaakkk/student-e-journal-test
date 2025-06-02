document.addEventListener('DOMContentLoaded', () => {
    const themeToggle = document.getElementById('theme-toggle');
    const html = document.documentElement;
    const themeIcon = themeToggle.querySelector('.theme-icon');

    // Load saved theme from localStorage
    const savedTheme = localStorage.getItem('theme') || 'dark';
    html.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    // Toggle theme on button click
    themeToggle.addEventListener('click', () => {
        const currentTheme = html.getAttribute('data-theme');
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        html.setAttribute('data-theme', newTheme);
        localStorage.setItem('theme', newTheme);
        updateThemeIcon(newTheme);
    });

    function updateThemeIcon(theme) {
        if (theme === 'light') {
            themeIcon.innerHTML = `
                <path d="M12 2a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0V3a1 1 0 0 1 1-1zm0 16a1 1 0 0 1 1 1v2a1 1 0 0 1-2 0v-2a1 1 0 0 1 1-1zM4.93 4.93a1 1 0 0 1 1.414 0l1.414 1.414a1 1 0 0 1-1.414 1.414L4.93 6.344a1 1 0 0 1 0-1.414zm12.728 12.728a1 1 0 0 1 1.414 0l1.414 1.414a1 1 0 0 1-1.414 1.414l-1.414-1.414a1 1 0 0 1 0-1.414zM2 12a1 1 0 0 1 1-1h2a1 1 0 0 1 0 2H3a1 1 0 0 1-1-1zm18 0a1 1 0 0 1 1-1h2a1 1 0 0 1 0 2h-2a1 1 0 0 1-1-1zm-2.072-7.07a1 1 0 0 1 0 1.414l-1.414 1.414a1 1 0 0 1-1.414-1.414L16.516 4.93a1 1 0 0 1 1.414 0zm-12.728 12.728a1 1 0 0 1 0 1.414l-1.414 1.414a1 1 0 0 1-1.414-1.414l1.414-1.414a1 1 0 0 1 1.414 0z"/>
            `;
        } else {
            themeIcon.innerHTML = `
                <path d="M12 7a5 5 0 0 1 5 5c0 1.2-.43 2.3-1.14 3.16A5 5 0 0 1 7 12a5 5 0 0 1 5-5zm0-2a7 7 0 0 0-7 7c0 1.67.59 3.2 1.57 4.43A7 7 0 0 0 12 19a7 7 0 0 0 7-7 7 7 0 0 0-7-7z"/>
            `;
        }
    }
});