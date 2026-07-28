CREATE TABLE bets
(
    technical_id               BIGINT                         NOT NULL,
    user_technical_id          BIGINT                         NOT NULL,
    match_technical_id         BIGINT                         NOT NULL,
    selected_team_technical_id BIGINT                         NOT NULL,
    amount                     BIGINT                         NOT NULL,
    status                     VARCHAR(20)                    NOT NULL,
    created_at                 TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    resolved_at                TIMESTAMP(6) WITHOUT TIME ZONE,
    coefficient                DOUBLE PRECISION               NOT NULL,
    potential_gain             BIGINT                         NOT NULL,
    id                         VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_bets PRIMARY KEY (technical_id)
);

CREATE TABLE matches
(
    technical_id            BIGINT                         NOT NULL,
    team1_technical_id      BIGINT                         NOT NULL,
    team2_technical_id      BIGINT                         NOT NULL,
    tournament_technical_id BIGINT                         NOT NULL,
    game                    VARCHAR(40)                    NOT NULL,
    scheduled_at            TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    status                  VARCHAR(20)                    NOT NULL,
    team1_score             INTEGER,
    team2_score             INTEGER,
    live_started_at         TIMESTAMP(6) WITHOUT TIME ZONE,
    winner_technical_id     BIGINT,
    id                      VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_matches PRIMARY KEY (technical_id)
);

CREATE TABLE players
(
    technical_id      BIGINT       NOT NULL,
    nickname          VARCHAR(50)  NOT NULL,
    full_name         VARCHAR(100) NOT NULL,
    role              VARCHAR(50),
    country_code      VARCHAR(2)   NOT NULL,
    team_technical_id BIGINT       NOT NULL,
    id                VARCHAR(255) NOT NULL,
    CONSTRAINT pk_players PRIMARY KEY (technical_id)
);

CREATE TABLE teams
(
    technical_id BIGINT       NOT NULL,
    name         VARCHAR(80)  NOT NULL,
    tag          VARCHAR(10)  NOT NULL,
    game         VARCHAR(40)  NOT NULL,
    id           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_teams PRIMARY KEY (technical_id)
);

CREATE TABLE tournaments
(
    technical_id BIGINT       NOT NULL,
    name         VARCHAR(100) NOT NULL,
    game         VARCHAR(40)  NOT NULL,
    start_date   date         NOT NULL,
    end_date     date         NOT NULL,
    id           VARCHAR(255) NOT NULL,
    CONSTRAINT pk_tournaments PRIMARY KEY (technical_id)
);

CREATE TABLE users
(
    technical_id     BIGINT                         NOT NULL,
    external_auth_id VARCHAR(255)                   NOT NULL,
    username         VARCHAR(50)                    NOT NULL,
    email            VARCHAR(255)                   NOT NULL,
    birth_date       date                           NOT NULL,
    first_name       VARCHAR(40)                    NOT NULL,
    last_name        VARCHAR(40)                    NOT NULL,
    kibbles_balance  BIGINT                         NOT NULL,
    role             VARCHAR(20)                    NOT NULL,
    created_at       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    status           VARCHAR(20)                    NOT NULL,
    blocked_reason   VARCHAR(255),
    id               VARCHAR(255)                   NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (technical_id)
);

ALTER TABLE bets
    ADD CONSTRAINT uc_bets_id UNIQUE (id);

ALTER TABLE matches
    ADD CONSTRAINT uc_matches_id UNIQUE (id);

ALTER TABLE players
    ADD CONSTRAINT uc_players_id UNIQUE (id);

ALTER TABLE players
    ADD CONSTRAINT uc_players_nickname UNIQUE (nickname);

ALTER TABLE teams
    ADD CONSTRAINT uc_teams_id UNIQUE (id);

ALTER TABLE teams
    ADD CONSTRAINT uc_teams_name UNIQUE (name);

ALTER TABLE teams
    ADD CONSTRAINT uc_teams_tag UNIQUE (tag);

ALTER TABLE tournaments
    ADD CONSTRAINT uc_tournaments_id UNIQUE (id);

ALTER TABLE tournaments
    ADD CONSTRAINT uc_tournaments_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_externalauthid UNIQUE (external_auth_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_id UNIQUE (id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE bets
    ADD CONSTRAINT FK_BETS_ON_MATCH_TECHNICAL FOREIGN KEY (match_technical_id) REFERENCES matches (technical_id);

ALTER TABLE bets
    ADD CONSTRAINT FK_BETS_ON_SELECTED_TEAM_TECHNICAL FOREIGN KEY (selected_team_technical_id) REFERENCES teams (technical_id);

ALTER TABLE bets
    ADD CONSTRAINT FK_BETS_ON_USER_TECHNICAL FOREIGN KEY (user_technical_id) REFERENCES users (technical_id);

ALTER TABLE matches
    ADD CONSTRAINT FK_MATCHES_ON_TEAM1_TECHNICAL FOREIGN KEY (team1_technical_id) REFERENCES teams (technical_id);

ALTER TABLE matches
    ADD CONSTRAINT FK_MATCHES_ON_TEAM2_TECHNICAL FOREIGN KEY (team2_technical_id) REFERENCES teams (technical_id);

ALTER TABLE matches
    ADD CONSTRAINT FK_MATCHES_ON_TOURNAMENT_TECHNICAL FOREIGN KEY (tournament_technical_id) REFERENCES tournaments (technical_id);

ALTER TABLE matches
    ADD CONSTRAINT FK_MATCHES_ON_WINNER_TECHNICAL FOREIGN KEY (winner_technical_id) REFERENCES teams (technical_id);

ALTER TABLE players
    ADD CONSTRAINT FK_PLAYERS_ON_TEAM_TECHNICAL FOREIGN KEY (team_technical_id) REFERENCES teams (technical_id);