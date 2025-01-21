CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    email    VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role     VARCHAR(20)  NOT NULL,
    activation_code VARCHAR(100)  NOT NULL
);

CREATE TABLE tasks
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(100) NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    priority    VARCHAR(20)  NOT NULL,
    author_id   BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    assignee_id BIGINT       REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE comments
(
    id        BIGSERIAL PRIMARY KEY,
    content   TEXT   NOT NULL,
    author_id BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    task_id   BIGINT NOT NULL REFERENCES tasks (id) ON DELETE CASCADE
);
