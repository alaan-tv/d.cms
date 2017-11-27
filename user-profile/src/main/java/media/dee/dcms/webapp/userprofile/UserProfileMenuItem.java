package media.dee.dcms.webapp.userprofile;

import media.dee.dcms.webapp.cms.MenuItem;

import java.util.Arrays;
import java.util.List;

public class UserProfileMenuItem implements MenuItem{
    @Override
    public List<String> getJavascriptModules() {
        return Arrays.asList("js/layout/userprofile/userprofile");
    }
}
