# d.CMS Installation

* Install JDK 8
* Install Maven 3x
* Install Karaf 4.2.0
* Install Node.js 8.x
* create directory for d.cms and plugins 'd.cms.project'
* inside 'd.cms.project' clone d.cms project
* inside 'd.cms.project' create 'd.plugins' directory
* inside 'd.plugins' clone d.user.profile project
* inside 'd.cms.project/d.cms' run ``mvn clean install``
* inside 'd.cms.project/d.plugins' run ``mvn clean install``
* start karaf ``karaf``
* enable karaf features
    * web: enable jetty full webserver, command:<br/>
        ``feature:install war``
    * celler: clustering support by karaf, command: <br/>
    ``feature:repo-add cellar``<br/>
    ``feature:install cellar``
* build and install d.cms, command:<br/>
    ``mvn clean install``
* install jackson module, commands:<br/>
    ``feature:repo-add mvn:org.code-house.jackson/features/2.8.0/xml/features``<br/>
    ``feature:install jackson-databind``
* build and install plugins you need.
* install core module, command:<br/>
  ``bundle:install mvn:media.dee.dcms/core/0.0.1-SNAPSHOT``
* install cms-web-app module, command:<br/>
    ``bundle:install mvn:media.dee.dcms/admin/0.0.1-SNAPSHOT``
* navigate 'http://localhost:{KARAF_HTTP:8181}/cms'


# Docker Installation
* cd to {d.cms project}
* run command:<br/>
    ```bash 
        docker build dee/cms .
    ```
* to create one docker container follow the following setps:
    * create container with name dcms1, command:<br/>
        ```bash
        docker run -d -t --name dcms1 -p 1099:1099 -p 8181:8181 -p 44444:44444 -v /work/env/docker.deploy:/deploy dee/cms
        ```
    * start the container
        ```bash
        docker start dcms1
        ```
    * connect to container ssh
    ```bash
    docker exec -it dcms1 bash
    ```
    * install karaf required features
        ```bash
            ./opt/karaf/bin/client feature:install war; \
                ./opt/karaf/bin/client feature:repo-add cellar; \
                ./opt/karaf/bin/client feature:install cellar; \
                ./opt/karaf/bin/client feature:repo-add mvn:org.code-house.jackson/features/2.8.0/xml/features; \
                ./opt/karaf/bin/client feature:install jackson-databind; \
                ./opt/karaf/bin/client feature:repo-add cellar
        ```