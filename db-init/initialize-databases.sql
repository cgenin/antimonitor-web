CREATE ROLE admin WITH LOGIN PASSWORD 'admin';

CREATE ROLE antimonitor WITH LOGIN PASSWORD 'antimonitor' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
CREATE DATABASE antimonitor;
GRANT ALL PRIVILEGES ON DATABASE antimonitor TO antimonitor ;
GRANT ALL PRIVILEGES ON DATABASE antimonitor TO admin;
