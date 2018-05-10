package media.dee.dcms.webapp.cms.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CMSApplicationActivator implements BundleActivator {

    public void start(BundleContext context) throws Exception {

        System.out.println("****************************************************************************");
        System.out.println("******************************** d.CMS Admin *******************************");
        System.out.println("****************************************************************************");

    }

    public void stop(BundleContext context) throws Exception {

        System.out.println("****************************************************************************");
        System.out.println("**************************** d.CMS Admin STOPPED ***************************");
        System.out.println("****************************************************************************");


    }
}
