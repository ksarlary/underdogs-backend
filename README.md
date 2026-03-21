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
