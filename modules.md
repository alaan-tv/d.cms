#Architecture
The Pages displayed on  Dee.CMS site are built from a combination of multiple components. Dee.CMS provides multiple methods  to create the pages, depending on  different types of styles, layouts and content for the different pages on the site.
#Main Architecture
The main components which contribute to the display of  Dee.CMS pages are:
* ## Theme
The Theme determines the look and feel of the page, include both the styling(CSS, SASS and JS) and common page elements. A theme can support multiple formats to produce for example HTML and AMP, each format has it's own templates and assets.
The Dee.CMS Themes provide a powerful way to consistent look and ell across a site or sites. Themes work with the Layout engine that allows content editor to create templates without any HTML knowledge.
* ## Layout engine model
   Dee.CMS provides layout concept, a different concept than template engines, but built on top of them,
   Where layout in Dee.CMS is assigned to one theme and the template itself defines where components placeholders/containers will appear,
   and the layout itself defines the visual layout by its CSS/SASS files and other assets, also templates support multi-formats and it's associated with one site.<br/>
   **Each layout is associated with one content type!** 
   
   **Layout** concept is mainly to create different layouts for pages and content types, where it become reusable and much modular
   ### Functional usage
   - **layout designer** will configure the layouts and defines the components placeholders/containers and the design of the layout along with its assets
   and defines what kind of containers/placeholders are supported if required, also he defines for each format the templates, assets, and the design
   - **layout configurer** users how can add components to placeholders/containers and provide the settings for each component.
* ## Placeholder/container
   The placeholder/container in Dee.CMS allows to specify what type of placeholder to be added to a page and how content of this pleaceholder type will display.
   
   **placeholder function:** 
     - Select what type of placeholder/content
     - Specify different formatting and styling for each placeholder/content type that will display in container
     - - Each contents/web components are placed inside containers, Containers are placed inside Template, and Template placed inside a pages, so each web component/ content displayed and formatted in a page via containers
       - Create different containers that format and display the same placeholder/content type in different ways
       - Set permissions to used it
* ## Placeholder/content Type
  Each web component/content in Dee.CMS is an instance of Placeholder/content Type. The content type determine:
   * What data can be added to web component of that type
   * The default permission will applied 
   * The default format and style 
   * What is thw workflow applied
   ..........
   .
   ..
   .
   ..
   
   .
   .
   
   

* ## Web components/Content:


>to modify 

It’s part of layout module and its most important part because it’s the core of the layout module, The Web components can interact with other Web components through API. Each Web components has own CSS and java Script and has templet, some component support AMP and IA. The sample list of Web components showing in list below:
- Poll
- Tweet 
- Post
- AD 
- Header
- footer
- etc.…
----------------------------------
# module design:

# Asset Module
# static pages module
# production bundle manager
# cluster sync technology
# Workflow module:
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

#explorer module:
#news module:
#VOD module:
#Revision Support and Modeling
#Drafting Support for Modules:

The drafting mechanism and auto save will be useful for the editor once write any post, so the system will save the user data (post) in cash (or somewhere maybe in DB if it’s efficient, user device) each period of time will have determined by the configurations, before final save or send the data for review.
this module has several API _like_:

- save draft 
- open draft
- list of draft
- etc.…

Each API should have (user, data) parameter at least. Once enabling this module will directly start saving automatically and also the user can call this API’s. With the development of this feature, the text entered in the Text Areas and the input fields will be maintained in front-end local storage as long as the form is in an overview page. When navigating away from the page or when clicking the save or the cancel button, the local storage or cashing would get cleared.

**Benefit:**

In case the browser crashes while entering data in a given page, when the user navigates back to the same form, a dialog box will be displayed to the user to confirm whether he wants to restore data.


# Preview Support for Drafts and Persisted Models:
There are two ways to launch the preview mode. One when create/edit any post and other one when create/edit the layout. This type of functionality allows us to preview any modification before going to publish it. This should call the render API for these types (normal HTML, AMP and IA) 
- Layout render:
  
   The layout render will show HTML result of designed any templet by the admin 
- Post render:

   should show post like as final result and will render full the page with any format we can selected (AMP, IA and Normal).

#Front Cache Module

#Server caching Module

#Configurations Module

#Site Module

#CMS Module

#Log Module
This module defines functions and API which implement an event logging for the application, Logging is means of tracking events that happen when the application runs. an event is described by a message. there is several types of log like:
- info
- warning
- error

The log module has configuration file to set save location, so any model can execute the log API after  by call it and set some parameters like 
- **level**: error, info, warning
- **message**: this will descript the event
- **format**: for date, time,
- **location**: DB, file
- **type**: system, user
- **from**: module name and class name, this information for technical purpose.
 
