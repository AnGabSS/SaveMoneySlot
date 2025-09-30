CREATE TABLE goals (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    target_amount NUMERIC(19, 2) NOT NULL,
    saved_amount NUMERIC(19, 2) NOT NULL,
    reason VARCHAR(255),
    deadline TIMESTAMP WITHOUT TIME ZONE,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions_categories (
    id BIGINT NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_transactions_categories_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT transactions_categories_type_check CHECK (type IN ('EXPENSE', 'INCOME', 'INVESTMENT'))
);

CREATE TABLE transactions (
    id BIGINT NOT NULL PRIMARY KEY,
    value NUMERIC(19, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES transactions_categories(id),
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE SEQUENCE goals_seq START 1 INCREMENT 50;
CREATE SEQUENCE transactions_categories_seq START 1 INCREMENT 50;
CREATE SEQUENCE transactions_seq START 1 INCREMENT 50;
