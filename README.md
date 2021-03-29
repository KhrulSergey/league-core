Freeton League Core "Tournament" - the system for manage FreeTon League Game Tournaments
---
Tags: #java, #springboot, #hibernate, #jpa, #springSecurity, #oauth2, #postgreSQL

Project uses: JDK11, Remote/Local PostgreSQL server.


### Requirements
**core-tournament**
- Available ports: 7701
- Installed: gradle and jdk11 for development
- Installed: docker and docker-compose Windows / Mac / Linux for deploy


### Configuration for start of project:

1. Use src/main/resources/application.yml for configure connection to DB PostgreSQL.
2. Use external (dev) PostgreSQL server or local server.
All docker infra-images (DB, Message Broker, e.t.c) contain in "freetonleague-core" project. See `docker` folder
```
# Open project directory
$ cd ./league-core
# Build image
$ docker-compose -p freeton-league -f docker/docker-compose.yml build
# Run docker image
$ docker-compose -p freeton-league -f docker/docker-compose.yml up
# Test project on: localhost:7701 
```
