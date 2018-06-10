FROM java:8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV KARAF_VERSION=4.2.0

RUN wget http://www-us.apache.org/dist/karaf/${KARAF_VERSION}/apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /opt/karaf; \
    tar --strip-components=1 -C /opt/karaf -xzf apache-karaf-${KARAF_VERSION}.tar.gz; \
    rm apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /deploy;

ADD setup.karaf /opt/karaf/
COPY org.apache.felix.fileinstall-deploy.cfg /opt/karaf/etc/org.apache.felix.fileinstall-deploy.cfg
COPY org.ops4j.pax.url.mvn.cfg /opt/karaf/etc/org.ops4j.pax.url.mvn.cfg
RUN cat /opt/karaf/setup.karaf | /opt/karaf/bin/karaf


VOLUME ["/deploy"]
EXPOSE 1099 8101 44444 5005
ENTRYPOINT ["/opt/karaf/bin/karaf", "debug"]