package media.dee.dcms.webapp.cms.internal;

import media.dee.dcms.components.AdminModule;
import media.dee.dcms.components.WebComponent;
import org.osgi.framework.Bundle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class ComponentResourceServlet extends HttpServlet {

    private Bundle bundle;
    private AdminModule adminModule;
    private WebComponent webComponent;

    public ComponentResourceServlet(WebComponent webComponent){
        this.webComponent = webComponent;
        this.bundle = AdminComponentConnector.getComponentBundle(webComponent);
        this.adminModule = AdminComponentConnector.getAdminModule(webComponent);
    }



    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        File mappedURL = AdminComponentConnector.getBundleWebAdminResrouceFile(webComponent);
        String resourcePath = req.getRequestURI().substring(mappedURL.getPath().length());

        URL url = bundle.getResource(new File(adminModule.resource(),resourcePath).getPath());
        try( InputStream in = url.openStream() ; OutputStream os = resp.getOutputStream() ){
            byte[] buffer = new byte[1024];
            while (true) {
                int readed = in.read(buffer);
                if (readed < 0)
                    break;
                os.write(buffer, 0, readed);
            }
            os.flush();
        } catch(IOException ex){
            throw ex;
        }
    }
}
