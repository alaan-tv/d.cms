package media.dee.dcms.filebundles;

import media.dee.dcms.CoreConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class FileBundleActivator implements BundleActivator{

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle");
    private List<Bundle> bundleList = new LinkedList<>();

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        File baseDir = new File(bundleContext.getProperty(CoreConstants.BASE_URI_PROPERTY));

        String bundles = resourceBundle.getString("bundles");
        StringTokenizer tokenizer = new StringTokenizer(bundles,";");
        while(tokenizer.hasMoreTokens()){
            String bundlePath = tokenizer.nextToken();
            File bundleFile = new File(baseDir, bundlePath);
            Bundle bundle = bundleContext.installBundle(bundleFile.toURI().toString());
            bundleList.add(bundle);
        }


        bundleList.forEach( bundle -> {
            try {
                bundle.start();
            } catch (BundleException e){
              e.printStackTrace(System.err);
            }
        } );
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
       /* bundleList.forEach( bundle -> {
            try {
                bundle.stop();
                bundle.uninstall();
            } catch (BundleException e){
                e.printStackTrace(System.err);
            }
        } );*/
    }
}
