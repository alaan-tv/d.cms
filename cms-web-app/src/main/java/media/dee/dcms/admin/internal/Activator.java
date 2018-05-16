package media.dee.dcms.admin.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {

        System.out.printf("****************************************************************************%n");
        System.out.printf("******************************** d.CMS Admin *******************************%n");
        System.out.printf("****************************** %15s *****************************%n", context.getBundle().getVersion());
        System.out.printf("****************************************************************************%n");

    }

    public void stop(BundleContext context) throws Exception {

        System.out.println("****************************************************************************");
        System.out.println("**************************** d.CMS Admin STOPPED ***************************");
        System.out.printf("****************************** %15s *****************************%n", context.getBundle().getVersion());
        System.out.println("****************************************************************************");


    }
}
