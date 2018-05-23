package media.dee.dcms.core.components;

import com.fasterxml.jackson.databind.JsonNode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WebComponent is the core of the dee.cms system, every visible component on the site is developed by WebComponent<br/>
 * WebComponent is edited and configured on admin interface via LayoutEngine<br/>.
 * Each layout setup is persisted on database with reference to the ID of the WebComponent.
 */
public interface WebComponent {


    interface Command {
        JsonNode execute(JsonNode... arguments);

        enum CommandType {
            Uninstall,
            Install

        }

        enum ComponentResourcesAction {
            Register,
            UnRegister //note semicolon needed only when extending behavior
        }

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        @interface For {
            String value();

            Class<? extends WebComponent> component();
        }
    }

}
