FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=target/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
COPY foundationdb-server.deb /tmp/foundationdb-server.deb
COPY foundationdb-clients.deb /tmp/foundationdb-clients.deb
ENTRYPOINT ["java","-cp","app:app/lib/*","com.task.track.Application"]
