# Freeton League Core "Tournament" - the system for manage FreeTon League Game Tournaments
---
Tags: #java, #springboot, #hibernate, #jpa, #springSecurity, #oauth2, #postgreSQL

Project uses: JDK11, Remote/Local PostgreSQL server.


### Requirements
*core-tournament**
- Available ports: 7701
- Installed: gradle and jdk11
- Installed: docker and docker-compose Windows / Mac / Linux


### Configuration for start of project:

1. Use src/main/resources/application.yml for configure connection to DB PostgreSQL.
2. Use external (dev) PostgreSQL server or local server.
All docker images contain in "freetonleague-core" project. See `infra-service-league` module 
```
cd ./freetonleague-core/infra-service-league
docker-compose build
docker-compose up -d
```
