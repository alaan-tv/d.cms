package media.dee.dcms.filebundles;

import media.dee.dcms.CoreConstants;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

public class FileBundleActivator implements BundleActivator{

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle");
    private List<Bundle> bundleList = new LinkedList<>();
    private WatchThread watchTread;
    private WatchService watcherService;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        watcherService = FileSystems.getDefault().newWatchService();
        watchTread = new WatchThread(watcherService);

        /**
         * baseDir is the root directory of the project.
         */
        File baseDir = new File(bundleContext.getProperty(CoreConstants.BASE_URI_PROPERTY)).getParentFile().getParentFile().getParentFile();

        String bundles = resourceBundle.getString("bundles");
        StringTokenizer tokenizer = new StringTokenizer(bundles,";");
        while(tokenizer.hasMoreTokens()){
            File bundleFile = new File(baseDir, tokenizer.nextToken() ).getCanonicalFile();
            Bundle bundle = bundleContext.installBundle(bundleFile.toURI().toString());
            bundleList.add(bundle);

            Path bundlePath = FileSystems.getDefault().getPath(bundleFile.getParentFile().getCanonicalPath());
            bundlePath.register(watcherService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);bundlePath.register(watcherService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);
            watchTread.watchBundle(bundle);
        }

        bundleList.forEach( bundle -> {
            try {
                bundle.start();
            } catch (BundleException e){
              e.printStackTrace(System.err);
            }
        } );

        watchTread.start();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
       watchTread.interrupt();
        watcherService.close();
    }
}
