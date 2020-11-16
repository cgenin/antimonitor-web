FROM node:12-alpine AS builderWeb
WORKDIR /usr/app
COPY web /usr/app
VOLUME "/root/yarn-cache"
ENV YARN_CACHE_FOLDER='/root/yarn-cache'
RUN yarn && yarn run build

FROM maven:3.6.3-openjdk-11 AS builderServer
WORKDIR /usr/app

RUN rm -Rf src/main/resources/META-INF/resources
COPY --from=builderWeb /usr/app/dist/spa src/main/resources/META-INF/resources
VOLUME "$USER_HOME_DIR/.m2"
COPY pom.xml .
RUN mvn verify clean --fail-never
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-slim

# Configure the JAVA_OPTIONS, you can add -XshowSettings:vm to also display the heap size.
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0"
WORKDIR /usr/app
COPY --from=builderServer /usr/app/target/lib/* /usr/app/lib/
COPY --from=builderServer /usr/app/target/*-runner.jar /usr/app/app.jar

EXPOSE 8080
VOLUME /target


ENTRYPOINT [ "java", "-jar", "app.jar" ]



