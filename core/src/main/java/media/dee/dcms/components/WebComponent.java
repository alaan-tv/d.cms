package media.dee.dcms.components;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * WebComponent is the core of the dee.cms system, every visible component on the site is developed by WebComponent<br/>
 * WebComponent is edited and configured on admin interface via LayoutEngine<br/>.
 * Each layout setup is persisted on database with reference to the ID of the WebComponent.
 */
public interface WebComponent {

    interface Command {
        JsonValue execute(JsonValue ...arguments);

        @Target(ElementType.TYPE)
        @Retention(RetentionPolicy.RUNTIME)
        public @interface For {
            String value();
            public Class<? extends WebComponent> component();
        }
    }

}
