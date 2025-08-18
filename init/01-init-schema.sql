CREATE SCHEMA IF NOT EXISTS taskflow AUTHORIZATION postgres;
ALTER ROLE postgres SET search_path TO taskflow;

SET search_path TO taskflow;