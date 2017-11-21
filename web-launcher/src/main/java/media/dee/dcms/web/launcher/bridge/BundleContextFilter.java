package media.dee.dcms.web.launcher.bridge;

import org.osgi.framework.BundleContext;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "BundleContextFilter", urlPatterns = {"/*"})
public class BundleContextFilter implements Filter {
    public static final ThreadLocal<BundleContext> BUNDLE_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if( servletRequest instanceof HttpServletRequest)
            System.out.printf("Request Path: %s%n", ((HttpServletRequest)servletRequest).getPathInfo() );
        BundleContext bundleContext = (BundleContext) servletRequest.getServletContext().getAttribute(BundleContext.class.getName());
        BUNDLE_CONTEXT_THREAD_LOCAL.set(bundleContext);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
