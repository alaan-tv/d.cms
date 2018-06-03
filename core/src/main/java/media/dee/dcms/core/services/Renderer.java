package media.dee.dcms.core.services;

import java.io.OutputStream;
import java.util.Map;

public interface Renderer {
    void render(OutputStream outputStream, Map<String, Object> model);
}
