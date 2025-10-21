-- ==== Tabelas de Usuários e Roles ====
CREATE SEQUENCE IF NOT EXISTS roles_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS roles (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('roles_seq'),
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT roles_name_check CHECK (name IN ('ADMIN', 'USER'))
);

CREATE SEQUENCE IF NOT EXISTS users_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS users (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('users_seq'),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    birthdate DATE,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP,
    updated_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);

CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Insere as roles básicas se não existirem
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER') ON CONFLICT (name) DO NOTHING;


-- ==== Tabelas de Champions e Parties ====
CREATE SEQUENCE IF NOT EXISTS champion_levels_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS champion_levels (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('champion_levels_seq'),
    title VARCHAR(255) NOT NULL,
    points_needed INT NOT NULL DEFAULT 0,
    points_max INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS champions_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS champions (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('champions_seq'),
    nickname VARCHAR(255) NOT NULL,
    points INT NOT NULL DEFAULT 0 CHECK (points >= 0),
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE
);

CREATE SEQUENCE IF NOT EXISTS parties_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS parties (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('parties_seq'),
    name VARCHAR(255) NOT NULL,
    points INT NOT NULL DEFAULT 0 CHECK (points >= 0),
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);

CREATE SEQUENCE IF NOT EXISTS parties_levels_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS parties_levels (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('parties_levels_seq'),
    title VARCHAR(255) NOT NULL,
    points_needed INT NOT NULL DEFAULT 0,
    points_max INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);

CREATE TABLE IF NOT EXISTS parties_champions (
    party_id BIGINT NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    champion_id BIGINT NOT NULL REFERENCES champions(id) ON DELETE CASCADE,
    PRIMARY KEY (party_id, champion_id)
);


-- ==== SEÇÃO DE TRANSAÇÕES E CATEGORIAS (MOVENDO PARA CIMA) ====
CREATE SEQUENCE IF NOT EXISTS transactions_categories_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS transactions_categories (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('transactions_categories_seq'),
    party_id BIGINT NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    UNIQUE (party_id, name, type),
    CONSTRAINT transactions_categories_type_check CHECK (type IN ('INCOME', 'EXPENSE', 'INVESTMENT'))
);


-- ==== SEÇÃO DE METAS (GOALS) ====
CREATE SEQUENCE IF NOT EXISTS goals_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS goals (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('goals_seq'),
    party_id BIGINT NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    reason VARCHAR(255),
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP,
    updated_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);

CREATE TABLE IF NOT EXISTS saving_goals (
    goal_id BIGINT PRIMARY KEY REFERENCES goals(id) ON DELETE CASCADE,
    target_amount NUMERIC(19, 2) NOT NULL CHECK (target_amount > 0),
    saved_amount NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP,
    is_finished BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS spending_limit_goals (
    goal_id BIGINT PRIMARY KEY REFERENCES goals(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES transactions_categories(id),
    limit_type VARCHAR(50) NOT NULL,
    limit_amount NUMERIC(19, 2) CHECK (limit_amount IS NULL OR limit_amount > 0),
    limit_percentage NUMERIC(5, 2) CHECK (limit_percentage IS NULL OR (limit_percentage > 0 AND limit_percentage <= 100)),
    initial_date DATE NOT NULL,
    final_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP,
    CONSTRAINT spending_limit_goals_type_check CHECK (limit_type IN ('AMOUNT', 'PERCENTUAL'))
);


-- ==== SEÇÃO DE TRANSAÇÕES (DEFINIÇÃO FINAL) ====
CREATE SEQUENCE IF NOT EXISTS transactions_seq START 1 INCREMENT 50;
CREATE TABLE IF NOT EXISTS transactions (
    id bigint NOT NULL PRIMARY KEY DEFAULT nextval('transactions_seq'),
    party_id BIGINT NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    category_id BIGINT NOT NULL REFERENCES transactions_categories(id) ON DELETE RESTRICT,
    description VARCHAR(255) NOT NULL,
    value NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT LOCALTIMESTAMP
);


-- ==== Índices ====
CREATE INDEX IF NOT EXISTS idx_users_roles_role_id ON users_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_party_champions_champion_id ON parties_champions(champion_id);
CREATE INDEX IF NOT EXISTS idx_goals_party_id ON goals(party_id);
CREATE INDEX IF NOT EXISTS idx_categories_party_id ON transactions_categories(party_id);
CREATE INDEX IF NOT EXISTS idx_transactions_party_id ON transactions(party_id);
CREATE INDEX IF NOT EXISTS idx_transactions_category_id ON transactions(category_id);