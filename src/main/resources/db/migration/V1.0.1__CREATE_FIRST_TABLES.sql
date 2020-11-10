CREATE TABLE IF NOT EXISTS WEB_APPLICATION_QUEUE
(
    id         varchar(256) NOT NULL PRIMARY KEY,
    created_at TIMESTAMP,
    state      varchar(12),
    artifactId         varchar(256),
    groupId         varchar(256),
    version         varchar(256),
    packagesJson json
);


CREATE TABLE IF NOT EXISTS TABLES (
  ID           VARCHAR(255) NOT NULL PRIMARY KEY,
  NAME         TEXT         NOT NULL,
  SERVICE      TEXT         NOT NULL,
  latestUpdate BIGINT
);

CREATE TABLE IF NOT EXISTS APIS (
  ID        VARCHAR(255) NOT NULL PRIMARY KEY,
  METHOD    VARCHAR(10),
  FULLURL   TEXT,
  IDPROJECT VARCHAR(255),
  document  json
);

CREATE TABLE IF NOT EXISTS PROJECTS (
  ID       VARCHAR(767) PRIMARY KEY,
  NAME     VARCHAR(200),
  document json
);
CREATE TABLE IF NOT EXISTS VERSIONS (
  ID        VARCHAR(255) PRIMARY KEY,
  IDPROJECT VARCHAR(255),
  NAME      VARCHAR(200),
  document  json
);
CREATE TABLE IF NOT EXISTS DEPENDENCIES (
  RESOURCE VARCHAR(200),
  USED_BY  VARCHAR(200),
  document LONGTEXT,
  PRIMARY KEY (RESOURCE, USED_BY)
);
CREATE TABLE IF NOT EXISTS FRONT_APPS (
  ID       VARCHAR(255) PRIMARY KEY,
  NAME     VARCHAR(255),
  LATEST   LONG,
  VERSION  VARCHAR(255),
  document json
);

CREATE TABLE IF NOT EXISTS QUEUE
(
    ID       serial primary key,
    state    int      null,
    document json null,
    LATEST_UPDATE bigint
);

CREATE TABLE IF NOT EXISTS STORED_SERVICE_EVENTS (
  ID            SERIAL PRIMARY KEY,
  state         INTEGER,
  document      json,
  ARCHIVE       BOOLEAN,
  LATEST_UPDATE BIGINT,
  ORIGINAL_ID BIGINT
);

CREATE TABLE IF NOT EXISTS CONFIGURATION (
    ID int PRIMARY KEY,
    configuration json
);

INSERT INTO CONFIGURATION (id, configuration) VALUES (1,'{}')
ON CONFLICT ON CONSTRAINT configuration_pkey
    DO NOTHING;

