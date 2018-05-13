[![Build Status](https://semaphoreci.com/api/v1/anasaswad/d-cms/branches/master/shields_badge.svg)](https://semaphoreci.com/anasaswad/d-cms)

<p align="center">
  <a href="https://gulpjs.com">
    <img height="256" width="256" src="https://raw.githubusercontent.com/alaan-tv/d.cms/master/cms-web-app/src/main/front-end/public/img/logo.svg">
  </a>
  <p align="center">The streaming build system</p>
</p>

# Dee.CMS
Dee.CMS a content management system built for media companies, to provide easy deployment and pluginable system for news and TV sites, scalable and reliable system, It's focused on user experience, and offers precise control for designers and developers, built on Amazon Web Services AWS.
# Features
* A fast, attractive interface for editors
* Configure content types front-end through layout engine
* Simple, configurable permissions
* Workflow support
* Multi-site and multi-language support
* Full template support and customizable templates


# Getting started
* ## Requirements
 Like many computer programs, Dee.CMS requires certain hardware and software specifications in order to install and run. These are basic requirements, though, so you may want to use our recommended suggestions to improve performance. The following list of requirements and recommendations apply to Dee.CMS.
  * **Operating System:** Windows and OS X
  * **Hardware:** Multi-Core processor, 4GB Ram or more and 100 GB HD
  * **Application Server:** Apache Tomcat 8+
  * **Client Browser:** chrome, safari and firefox
* ## Installation
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
    ``feature:repo-add mvn:org.code-house.jackson/features/2.8.0/xml/features``
    ``feature:install jackson-databind``
  * build and install plugins you need.
  * install core module, command:<br/>
      ``bundle:install mvn:media.dee.dcms/core/0.0.1-SNAPSHOT``
  * install cms-web-app module, command:<br/>
    ``bundle:install mvn:media.dee.dcms/admin/0.0.1-SNAPSHOT``
  * navigate 'http://localhost:{KARAF_HTTP:8181}/cms'