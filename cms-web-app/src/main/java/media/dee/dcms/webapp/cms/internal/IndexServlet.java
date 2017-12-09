package media.dee.dcms.webapp.cms.internal;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IndexServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        InputStream in = getServletContext().getResourceAsStream("/webapp/index.html");
        OutputStream os = resp.getOutputStream();
        byte[] buffer = new byte[1024];
        while(true){
            int readed = in.read(buffer);
            if( readed < 0 )
                break;
            os.write(buffer, 0 , readed);
        }
        os.flush();
        in.close();
        os.close();
    }
}
