package media.dee.dcms.components;

import java.util.Map;

/**
 * WebComponent is the core of the dee.cms system, every visible component on the site is developed by WebComponent<br/>
 * WebComponent is edited and configured on admin interface via LayoutEngine<br/>.
 * Each layout setup is persisted on database with reference to the ID of the WebComponent.
 */
@UUID(value = "" )
public interface WebComponent {
    void setConfig(Map<String,Object> config);
}
