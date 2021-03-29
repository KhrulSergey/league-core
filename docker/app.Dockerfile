FROM gradle:6.8.3-jdk11-openj9 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle assemble --no-daemon


# Running stage
FROM openjdk:11.0.6-jre-buster
EXPOSE 8080
RUN mkdir /app

COPY --from=build /home/gradle/src/build/libs/*.jar /app/league-core.jar

# Labels to allow ELK analyze logs with json decode processor
LABEL   log_json="allow" \
        log_type="spring"

ENV JAVA_OPTS=""
ENV MAX_RAM_PERCENTAGE=70

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app/league-core.jar" ]
