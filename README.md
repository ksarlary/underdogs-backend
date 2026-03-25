# Commands
## Run the application
`mvn spring-boot:run`

## Run tests
`mvn test`

## Run tests with coverage (JaCoCo)
`mvn clean test`

Open coverage report: **target/site/jacoco/index.html**

## Check formatting (Spotless)
`mvn spotless:check`

## Format code (Spotless)
`mvn spotless:apply`


# Routes:
## User
* **GET /api/v1/users/me** — Returns the currently authenticated user (Creates user in DB if not exists, syncs data from Keycloak)
* **GET /api/v1/users/{id}** — ADMIN only — Return a user by ID
* **GET /api/v1/users** — ADMIN only — Returns all users
* **POST /api/v1/users** — Creates a new user (useless, since we use IAM)
* **PUT /api/v1/users/{id}** — Updates a user
* **POST /api/v1/{id}/kibbles/credit** — Adds (credit) kibbles to a user’s balance

## Player
* **GET /api/v1/players** — Returns all players
* **POST /api/v1/players** — ADMIN only — Creates a new player
* **GET /api/v1/players/{id}** — Returns a player by ID
* **PATCH /api/v1/players/{id}** — ADMIN only — Updates a player
* **DELETE /api/v1/players/{id}** — ADMIN only — Deletes a player

## Team
* **GET /api/v1/teams** — Returns all teams
* **POST /api/v1/teams** — ADMIN only — Creates a new team
* **GET /api/v1/teams/{id}** — Returns a team by ID
* **PATCH /api/v1/teams/{id}**  — ADMIN only — Updates a team
* **DELETE /api/v1/teams/{id}**  — ADMIN only — Deletes a team

## Tournament
* **GET /api/v1/tournaments** — Returns all tournaments
* **POST /api/v1/tournaments** — ADMIN only — Creates a new tournament
* **GET /api/v1/tournaments/{id}** — Returns a tournament by ID
* **PATCH /api/v1/tournaments/{id}**  — ADMIN only — Updates a tournament
* **DELETE /api/v1/tournaments/{id}**  — ADMIN only — Deletes a tournament
  
## Match
* **GET /api/v1/matches** — Returns all matches
* **POST /api/v1/matches** — ADMIN only — Creates a new match
* **GET /api/v1/matches/{id}** — Returns a match by ID
* **PATCH /api/v1/matches/{id}**  — ADMIN only — Updates a match
* **DELETE /api/v1/matches/{id}**  — ADMIN only — Deletes a match
