package org.apache.zookeeper.server;

import java.io.File;

public class ServerConfigPub extends ServerConfig {
	
	private File dataDir;
	
	public ServerConfigPub(File dataDir) {
		this.dataDir = dataDir;
	}
	
	@Override
	public File getDataDir() {
		return dataDir;
	}
	
	public String getInitialConfig() {
		return initialConfig;
	}

}
