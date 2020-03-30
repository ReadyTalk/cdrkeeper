FROM openjdk:10-jre-slim
ARG VERSION
RUN apt-get update && apt-get install -y dumb-init && rm -rf /var/lib/apt/lists/*
COPY build/libs/cdrkeeper-${VERSION}-all.jar /

ADD run.sh /run.sh
RUN chmod 755 /run.sh
RUN touch /env.sh
ENV JVM_OPTS "-Xmx256m"
ENV VERSION ${VERSION}

ENTRYPOINT ["/run.sh"]
CMD ["/bin/bash", "-c", "java ${JVM_OPTS} -jar cdrkeeper-${VERSION}-all.jar"]

