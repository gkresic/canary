FROM docker.io/eclipse-temurin:21-jdk-alpine-3.21 AS jre-build

COPY build-jre /opt/

RUN /opt/build-jre

FROM docker.io/alpine:3.21

LABEL org.opencontainers.image.source=https://github.com/gkresic/canary
LABEL org.opencontainers.image.description="MVP Java REST server"
LABEL org.opencontainers.image.licenses=MIT

ENV JAVA_HOME=/opt/jre
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /opt/jre ${JAVA_HOME}

ENV APP_HOME=/opt/app

COPY build/install/canary-server/ ${APP_HOME}

WORKDIR ${APP_HOME}

ENTRYPOINT [ "bin/canary-server" ]
