package media.dee.dcms.webapp.cms.components;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import java.util.Arrays;
import java.util.List;


public class NavigationMenu implements EssentialComponent {
    public void activate(ComponentContext ctx) {
        System.out.println("NavigationMenu Activated.");
    }
    @Override
    public List<String> getJavascriptModules() {
        return Arrays.asList("js/layout/Menubar");
    }

}
