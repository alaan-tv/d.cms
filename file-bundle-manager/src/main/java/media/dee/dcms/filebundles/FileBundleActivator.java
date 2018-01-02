package media.dee.dcms.filebundles;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import java.io.File;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.*;

public class FileBundleActivator implements BundleActivator{

    private ResourceBundle resourceBundle = ResourceBundle.getBundle("bundle");
    private List<Bundle> bundleList = new LinkedList<>();
    private WatchThread watchTread;
    private WatchService watcherService;

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        watcherService = FileSystems.getDefault().newWatchService();
        watchTread = new WatchThread(watcherService);

        File baseLocation = new File(new URI(bundleContext.getBundle().getLocation()));

        /**
         * baseDir is the root directory of the project.
         */
        File baseDir = baseLocation.getParentFile().getParentFile().getParentFile();

        File bundlesDir = new File(baseLocation.getParentFile(), "bundles");
        File[] bundleFiles = bundlesDir.listFiles();
        Arrays.stream(bundleFiles != null ? bundleFiles : new File[0])
            .forEach( (bundleFile)->{
                try {
                    Bundle bundle = bundleContext.installBundle(bundleFile.toURI().toString());
                    bundleList.add(bundle);
                } catch (BundleException e) {
                    e.printStackTrace();
                }
            });

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
