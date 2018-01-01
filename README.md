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
  * Install Tomcat8
  * create directory for d.cms and plugins 'd.cms.project'
  * inside 'd.cms.project' clone d.cms project
  * inside 'd.cms.project' create 'd.plugins' directory
  * inside 'd.plugins' clone d.user.profile project
  * inside 'd.cms.project/d.cms' run ``mvn clean install``
  * inside 'd.cms.project/d.plugins' run ``mvn clean install``
  * configure {TOMCAT_HOME}/conf/context.xml and add the variable of file-bundle-manager/target/file-bundle-manager-{VERSION}.jar full path
  ``<Parameter name="deecms.bundle.manager.url" value="file://{FILE-BUNDLE-MANAGER--FULL-PATH}" override="false"/>``
  * to deploy copy 'd.cms.project/d.cms/web-launcher/target/web-launcher-{VERSION}.jar' to '{TOMCAT_HOME}/webapps' and name it 'ROOT.war'
  * start tomcat.
  * navigate 'http://localhost:{TOMCAT_PORT:8080}/cms'