# Front End
Dee.CMS has a layout engine powered by templates and themes in admin interface to enable front-end customization and empower it.
Each site configured on Dee.CMS has its own themes layouts and templates.

## Theme
Theme contains the main template of the site and its assets.
A theme can support multiple formats to produce for example HTML and AMP, each format has it's own templates and assets
- CSS/SASS files
- Images/Videos/Fonts
- Javascript modules

The front-end engineer shall develop the theme from the admin interface, where he defines the theme in a selected site and creates a workspace for the theme,
The theme is based on tiles concept and empowered by templating engine as he can choose (FreeMarker is the basic tempate engine), 

each format type has it's own templates folder and assets, the entry template name is "template" and he can add template tiles as it's required.
CSS files are supported natively and css engine also supports different formats of CSS such as SASS, where template engine will provide functions on the template layer
to link generated css files into the template engine, where css generation is versioned to avoid caching issues.

# Layout
Dee.CMS provides layout concept, a different concept than template engines, but built on top of them,
Where layout in Dee.CMS is assigned to one theme and the template itself defines where components placeholders/containers will appear,
and the layout itself defines the visual layout by its CSS/SASS files and other assets, also templates support multi-formats and it's associated with one site.<br/>
**Each layout is associated with one content type!** 

**Layout** concept is mainly to create different layouts for pages and content types, where it become reusable and much modular
### Functional usage
- **layout designer** will configure the layouts and defines the components placeholders/containers and the design of the layout along with its assets
and defines what kind of containers/placeholders are supported if required, also he defines for each format the templates, assets, and the design
- **layout configurer** users how can add components to placeholders/containers and provide the settings for each component.


# Content Types and layout
Each content type has a default layout, and each content can use different layout depends on the content type supports
for example:
- static pages have a default layout, but when a user creates a static page he can choose a template from other tempaltes inventory.
- News layout is configured by category or topic
- Each TV Show/Program is configured on creation or modification time.
- Program Episode is configured on Program/Show level.

