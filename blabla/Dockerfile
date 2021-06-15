FROM docker:19.03.12
RUN apk update && apk add --no-cache bash curl openjdk11

ARG GRADLE_VERSION=6.8.3

RUN curl -O "https://downloads.gradle-dn.com/distributions/gradle-$GRADLE_VERSION-bin.zip"
RUN mkdir /opt/gradle && unzip -d /opt/gradle gradle-${GRADLE_VERSION}-bin.zip
ENV PATH="${PATH}:/opt/gradle/gradle-$GRADLE_VERSION/bin"

#RUN gradle -v

RUN rm -rf /var/cache/apk/*
RUN rm -rf gradle-${GRADLE_VERSION}-bin.zip
