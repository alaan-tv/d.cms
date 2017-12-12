# Web components model:
It’s part of layout module and its most important part because it’s the core of the layout module, The Web components can interact with other Web components throw API. Each Web components has own CSS and java Script and has templet, some component support AMP and IA. The sample list of Web components showing in list below:
- Poll
- Tweet 
- Post
- AD 
- Header
- footer
- etc.…

# layout engine model:
The layout engine work like the container for the web components and allow to each component to communicate with other. There is configuration file include all the components and the location of each one. The layout engine allows us to create templet of layout to use it for display the web components 
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

#explorer module
#news module
#VOD module
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
There are several usages for preview mode, the user can use it when define the layout and also when the editor user start writ the post. So, there are two ways to launch the preview mode. One in Editor post and  one 
#Front Cache Module
#Server caching Module
#Configurations Module
#Site Module
#CMS Module
