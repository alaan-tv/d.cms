package media.dee.dcms.webapp.cms.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@WebServlet(name = "index servlet", urlPatterns = {"/"})
public class IndexServlet extends HttpServlet {



    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream in = getServletContext().getResourceAsStream("/webapp/index.html");
        OutputStream os = resp.getOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int readed = in.read(buffer);
            if (readed < 0)
                break;
            os.write(buffer, 0, readed);
        }
        os.flush();
        in.close();
        os.close();
    }
}
