CREATE SEQUENCE IF NOT EXISTS champions_levels_seq START 1 INCREMENT 50;

CREATE TABLE IF NOT EXISTS public.champions_levels (
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('champions_levels_seq'::regclass),
    title VARCHAR(255) NOT NULL,
    points_needed INTEGER NOT NULL DEFAULT 0
);

CREATE SEQUENCE IF NOT EXISTS champions_seq START 1 INCREMENT 50;

CREATE TABLE IF NOT EXISTS public.champions (
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('champions_seq'::regclass),
    nickname VARCHAR(255) NOT NULL,
    points INTEGER NOT NULL,
    user_id BIGINT NOT NULL UNIQUE,
    level_id BIGINT,
    CONSTRAINT champions_points_check CHECK (points >= 0),
    CONSTRAINT fk_champions_user FOREIGN KEY (user_id)
        REFERENCES public.users (id) ON UPDATE NO ACTION ON DELETE NO ACTION
);