package org.apache.zookeeper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

/**
 * @author gaellalire
 */
public class ZooKeeperVestigeInstaller {

    private File config;
    
    public ZooKeeperVestigeInstaller(final File config, final File data, final File cache) {
        this.config = config;
    }

    public void install() throws Exception {
        File entryDestination = new File(config, "zoo.cfg");
        OutputStream out = new FileOutputStream(entryDestination);
        IOUtils.copy(ZooKeeperVestigeInstaller.class.getResourceAsStream("/zoo.cfg"), out);
        IOUtils.closeQuietly(out);
    }

}
