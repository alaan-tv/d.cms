[![Build Status](https://semaphoreci.com/api/v1/dee-media/d-cms/branches/master/badge.svg)](https://semaphoreci.com/dee-media/d-cms)

<p align="center">
    <img height="256" width="256" src="https://raw.githubusercontent.com/dee-media/d.admin.gui/master/src/main/javascript/public/logo.png">
  <p align="center">Digital Media CMS</p>
</p>

# Dee.CMS
Dee.CMS a content management system built for media companies, to provide easy deployment and pluginable system for news, TV sites, Video-On-Demand sites, and more.<br/>

## Features
* a scalable and reliable system built on top of [Apache Karaf](https://karaf.apache.org/)
* multi is builitin: multi-site and multi-language.
* focused on building robust systems not a generic data model as other CMS softwares approach.
* front-end and website optimization in mind, which makes dee.CMS a perfect fit to digital media.
* components driven model for your site, which makes your site easier to update and develope and customize.
* customizable on many levels, as OSGI provides a modularity, functionality of the site can be customized by customizing the model which contains the functionality; and even provide additional functionality to the system by adding your customzied services.
* faster from Storage, Database and even server caching to CDN integration, all out of the box.
* Publishers focus on SEO and so we do.
* Accelarted Mobile Pages in mind, as we provide all of the requirements for AMP pages such as inline CSS, no-js, different templating, and even different CSS.
* Page Optimized: we provide out of the box tools for front-end such as [SASS](https://sass-lang.com/), JS minifiying, and optimized CSS to the page ( no staffed CSS for all the pages, only the required CSS to render the page ).
* Single Page Applicatin support: as we provide JS packaging tools ( Webpack ) out of the box and Javascript workspace to front-end engineer.
* Online never go offline: all front-end work from templating or developing your single page application to develping your site theme and desgin usign [SASS](https://sass-lang.com/) are online, provided by our Layout Designer and Javascript Workspace and integrated Javascript Packager.<br/>write your code, script your template using markup languages such as [freemarker](https://freemarker.apache.org/), test your work on real data and real use case scenarios, the publish your work to the live site.
* Marketing in mind: as a publisher you would create campaigns frequently, and landing pages should be optimized to user. here where d.CMS provide you a way to customize every view in an organized manner to front-end, by defining re-usable templates and associate any view to a template to get your landing page as you wish.
* Visual Layout Builder: define your site master layouts in markup language, and design your templates by the visual layout builder where place-holders gives you where to add components, customize them and ever preview your template in one click using real life data on your site. once you test your changes, one click to put live.
* Data flavors: as a digital media compnay you want to expose your content in many formats, and we provide you with out of the box tools, built in RSS and sitemaps and structured data.
  * RSS
  * Sitemaps
  * Structured Data
* Cloud Ready: d.CMS is ready to be deployed on Amazon Web Services AWS and it utlize AWS to the maxminum.
  * Clustered.
  * S3 Storage.
  * Scalable.
  * Full Cloud Front Integration.

## Installation
Check out [installation](docs/installation.md)

## Web components/Content:

 The Web component/content in Dee.CMS take many form and can be used in different way depending on the placeholder/content type, 
 Content can be any any object such as **post** ,**tweeter**, **youtube** and so on so far. below list of web component:
  - Navigation Menu
  - Model Listing
  - Comments Component
  - Tweeter
  - Imag gallery
  - Pull
  - Video
  - Social share

##  Workflow module:
The workflow engine responsible for defining and managing all the steps to work together as a unit, each step has input and output and the input some time maybe is output of another workflow. The step is an activity to do something. The workflow is executing by anther workflow or user has the permission, in other word the workflow is define the way how the process will work and only can work with predefine processes as listed below:
- Send email
- Post review
-	Publish 
-	Facebook publishes
-	YouTube publishes 
-	Tweeter publishes
-	Sent notification
-	Save post in DB
-	Save draft
-	Retrieve post data by (user, clients, date)
-	Retrieve draft data
-	Approve publish
-	etc.… 

## Drafting Support for Modules:

The drafting mechanism and auto save will be useful for the editor once write any content, in case the browser crashes while entering data in a given page, when the user navigates back to the same form, a dialog box will be displayed to the user to confirm whether he wants to restore data.


## Preview Support for Drafts and Persisted Models:
There are two ways to launch the preview mode. One when create/edit any post and other one when create/edit the layout. This type of functionality allows us to preview any modification before going to publish it. This should call the render API for these types (normal HTML, AMP and IA) 
- Layout render:
  
   The layout render will show HTML result of designed any templet by the admin 
- Post render:

   should show post like as final result and will render full the page with any format we can selected (AMP, IA and Normal).

## Features
d.CMS is a modular system, built on [OSGI technology](https://www.osgi.org/) using [Apache Karaf](https://karaf.apache.org/) as a runtime of OSGI, the following modules are the d.cms system:
* Front Cache
* Modular Admin interface
* Layout Builder
* Asset Managment
* Video Processing
* Static Pages
* Cluster Synchronization
* Cluster Management
* Explorer
* Video On Demand
* News
* Social Publishing: Facebook, Google+, Twitter.
* Revision & Drafting