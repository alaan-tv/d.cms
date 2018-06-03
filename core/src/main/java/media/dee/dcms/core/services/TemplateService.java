package media.dee.dcms.core.services;

import java.util.Map;

public interface TemplateService {

    StringBuffer render(String html, Map<String,Object> model);
}
