FROM java:8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV KARAF_VERSION=4.2.0

RUN wget http://www-us.apache.org/dist/karaf/${KARAF_VERSION}/apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /opt/karaf; \
    tar --strip-components=1 -C /opt/karaf -xzf apache-karaf-${KARAF_VERSION}.tar.gz; \
    rm apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /deploy;

RUN echo "feature:install war" >> /setup.bash;\
    echo "feature:repo-add cellar" >> /setup.bash;\
    echo "feature:install cellar" >> /setup.bash;\
    echo "feature:repo-add mvn:org.code-house.jackson/features/2.8.0/xml/features" >> /setup.bash;\
    echo "feature:install jackson-databind" >> /setup.bash
RUN cat /setup.bash | /opt/karaf/bin/karaf


VOLUME ["/deploy"]
EXPOSE 1099 8101 44444 5005
ENTRYPOINT ["/opt/karaf/bin/karaf"]


# docker run -d -t --name dcms1 -p 1099:1099 -p 8181:8181 -p 44444:44444 -v /work/env/docker.deploy:/deploy dee/cms
# docker run -d -t --name dcms2 -p 10099:1099 -p 8182:8181 -p 44445:44444 -v /work/env/docker.deploy:/deploy dee/cms