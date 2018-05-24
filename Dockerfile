FROM java:8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

ENV KARAF_VERSION=4.2.0

RUN wget http://www-us.apache.org/dist/karaf/${KARAF_VERSION}/apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /opt/karaf; \
    tar --strip-components=1 -C /opt/karaf -xzf apache-karaf-${KARAF_VERSION}.tar.gz; \
    rm apache-karaf-${KARAF_VERSION}.tar.gz; \
    mkdir /deploy;

#RUN bash /opt/karaf/bin/start
#
#RUN sleep 20
#
#RUN ps -aux
#
#RUN bash /opt/karaf/bin/client feature:install war
#RUN bash /opt/karaf/bin/client feature:repo-add cellar
#RUN bash /opt/karaf/bin/client feature:install cellar
#RUN bash /opt/karaf/bin/client feature:repo-add mvn:org.code-house.jackson/features/2.8.0/xml/features
#RUN bash /opt/karaf/bin/client feature:install jackson-databind
#RUN bash /opt/karaf/bin/client feature:repo-add cellar


VOLUME ["/deploy"]
EXPOSE 1099 8101 44444 5005
ENTRYPOINT ["/opt/karaf/bin/karaf"]


# docker run -d -t --name dcms1 -p 1099:1099 -p 8181:8181 -p 44444:44444 -v /work/env/docker.deploy:/deploy dee/cms
# docker run -d -t --name dcms2 -p 10099:1099 -p 8182:8181 -p 44445:44444 -v /work/env/docker.deploy:/deploy dee/cms