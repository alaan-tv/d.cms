package media.dee.dcms.filebundles;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class WatchThread extends Thread {
    private WatchService watchService;
    private HashMap<String, Bundle> bundles = new HashMap<>();

    public WatchThread(WatchService watchService) {
        this.watchService = watchService;
    }

    public void run() {
        while (true) {
            try {
                final WatchKey wk = watchService.take();
                for (WatchEvent<?> event : wk.pollEvents()) {
                    //we only register "ENTRY_MODIFY" so the context is always a Path.
                    final Path changed = (Path) event.context();
                    Bundle bundle = bundles.get(changed.toFile().getName());
                    if( bundle != null && event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                        try {
                            bundle.update();
                            System.out.printf("[%s] Bundle `%s-%s` is updated%n", LocalDateTime.now() , bundle.getSymbolicName(), bundle.getVersion().toString() );
                        } catch (BundleException e) {
                            e.printStackTrace(System.err);
                        }
                }
                // reset the key
                boolean valid = wk.reset();
                if (!valid) {
                    System.out.println("Key has been unregistered");
                }
            }catch (InterruptedException ex){
                return;
            }
        }
    }

    public void watchBundle(Bundle bundle) {
        try {
            bundles.put(new File(new URI(bundle.getLocation())).getName(), bundle);
        } catch (URISyntaxException e) {
            e.printStackTrace(System.err);
        }
    }
}
