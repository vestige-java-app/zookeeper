package org.apache.zookeeper.server;

import org.apache.zookeeper.server.persistence.FileTxnSnapLog;
import org.apache.zookeeper.server.util.JvmPauseMonitor;

public class ZooKeeperServerPub extends ZooKeeperServer {
	
    private ZooKeeperServerShutdownHandlerPub zkShutdownHandler;

	public ZooKeeperServerPub(JvmPauseMonitor jvmPauseMonitor, FileTxnSnapLog txnLogFactory, int tickTime,
			int minSessionTimeout, int maxSessionTimeout, int clientPortListenBacklog, ZKDatabase zkDb,
			String initialConfig) {
		super(jvmPauseMonitor, txnLogFactory, tickTime, minSessionTimeout, maxSessionTimeout, clientPortListenBacklog, zkDb,
				initialConfig);
	}

	public void registerServerShutdownHandler(ZooKeeperServerShutdownHandlerPub zkShutdownHandler) {
		this.zkShutdownHandler = zkShutdownHandler;
	}
	
	@Override
	protected void setState(State state) {
		super.setState(state);
        if (zkShutdownHandler != null) {
            zkShutdownHandler.handle(state);
        } else {
            LOG.debug(
                "ZKShutdownHandler is not registered, so ZooKeeper server"
                    + " won't take any action on ERROR or SHUTDOWN server state changes");
        }
	}
	
	@Override
	public boolean canShutdown() {
		return super.canShutdown();
	}
	
    public RequestProcessor getFirstProcessor() {
		return firstProcessor;
	}

}
