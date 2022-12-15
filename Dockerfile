FROM openjdk:11
MAINTAINER urlShortener
COPY build/libs/urlShortener-0.0.1-SNAPSHOT.jar urlShortener-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/urlShortener-0.0.1-SNAPSHOT.jar"]