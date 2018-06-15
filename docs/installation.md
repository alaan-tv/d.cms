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
- Developer Docker: for easier development we recommend docker for developers and we have prepared easy tools to start wording without obstacles, by following these steps:
    - create a directory to place the build bundles
    - Setup Your IDE to output bundles into the created folder.
    - build docker image, setup a container, and enter container ssh by running the following command
        ```bash
          ./docker/dev/build.sh <image_name> <container_name> <bundles directory>
        ```
    - navigate `http://localhost:8080/cms` and `http://localhost:8080/system/console/bundles` to enter karaf webconsole.
- For staging or production, use docker/Dockerfile which will contain all d.cms bundles, and you can run the bash command:
    ```bash
      ./docker/dev/build.sh <image_name> <container_name>
    ```