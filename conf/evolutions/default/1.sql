# Roles and Permissions schema

# --- !Ups

CREATE TABLE UserPermission (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE SecurityRole (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO SecurityRole (name)
    VALUES ("user");

INSERT INTO SecurityRole (name)
VALUES ("admin");

# --- !Downs

DROP TABLE UserPermission;
DROP TABLE SecurityRole;