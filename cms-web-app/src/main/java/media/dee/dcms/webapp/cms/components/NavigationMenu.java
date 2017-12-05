package media.dee.dcms.webapp.cms.components;

import media.dee.dcms.components.AdminModule;
import org.osgi.service.component.ComponentContext;


@AdminModule("/webapp/js/layout/Menubar")
public class NavigationMenu implements GUIComponent {
    public void activate(ComponentContext ctx) {
        System.out.println("NavigationMenu Activated.");
    }
}
