
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS

RUN adduser -h /srv/java -u 10000 -g java -D java && chown -R java:java /srv/java
USER 10000

ADD target/hello-1.0-SNAPSHOT.jar spotify-automation.jar
EXPOSE 8080
#ENTRYPOINT exec java $JAVA_OPTS -jar memberservice.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar spotify-automation.jar

