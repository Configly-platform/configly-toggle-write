CREATE TABLE project_ref
(
    id     uuid primary key,
    status varchar(20) not null
);

CREATE TABLE environment_ref
(
    id         uuid primary key,
    status     varchar(20) not null,
    project_id uuid references project_ref (id),
    CONSTRAINT environment_ref_unique_per_project unique (project_id, id)
);

CREATE TABLE feature_toggle
(

    id             uuid primary key,
    name           varchar(100) not null,
    description    varchar(200) not null,
    project_id     uuid         not null,
    environment_id uuid         not null,
    type           varchar(50)  not null,
    current_value  varchar(200) not null,
    status         varchar(20)  not null,
    created_at     timestamp    not null,
    updated_at     timestamp    not null,
    CONSTRAINT feature_toggle_unique_per_env
        UNIQUE (environment_id, name),
    CONSTRAINT feature_toggle_fk_project_id
        FOREIGN KEY (project_id) REFERENCES project_ref (id),
    CONSTRAINT feature_toggle_fk_environment_id
        FOREIGN KEY (environment_id) REFERENCES environment_ref (id)
);

CREATE TABLE processed_events
(
    id uuid primary key
);