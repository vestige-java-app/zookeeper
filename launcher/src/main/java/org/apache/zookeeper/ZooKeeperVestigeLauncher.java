package org.apache.zookeeper;

import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;

import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerConfigPub;
import org.apache.zookeeper.server.ZooKeeperServerMainInterruptable;
import org.apache.zookeeper.server.admin.AdminServer.AdminServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author gaellalire
 */
public class ZooKeeperVestigeLauncher implements Callable<Void> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZooKeeperVestigeLauncher.class);
	
	private File config;

	private File data;
	
	static {
		// global modification is prohibited
		UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		try {
			Class.forName(NIOServerCnxnFactory.class.getName(), true, Thread.currentThread().getContextClassLoader());
		} catch (ClassNotFoundException e1) {
		}
        Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler);
	}

	public ZooKeeperVestigeLauncher(final File config, final File data, File cache) {
		this.config = config;
		this.data = data;
	}

	public Void call() throws Exception {
		File zooCfg = new File(config, "zoo.cfg");
		final ServerConfigPub config = new ServerConfigPub(data);
		config.parse(zooCfg.getAbsolutePath());

		final ZooKeeperServerMainInterruptable zooKeeperServerMain = new ZooKeeperServerMainInterruptable();

		Thread launcherThread = new Thread() {
			@Override
			public void run() {
				try {
					zooKeeperServerMain.runFromConfig(config);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (AdminServerException e) {
					e.printStackTrace();
				}
			}
		};
		launcherThread.start();
		try {
			synchronized (this) {
				wait();
			}
		} catch (InterruptedException e) {
			launcherThread.interrupt();
			while (true) {
				try {
					launcherThread.join();
					break;
				} catch (InterruptedException e1) {
					LOGGER.trace("Ignore interrupt", e1);
				}
			}
			Thread currentThread = Thread.currentThread();
			ThreadGroup threadGroup = currentThread.getThreadGroup();
			int activeCount = threadGroup.activeCount();
			while (activeCount != 1) {
				Thread[] list = new Thread[activeCount];
				int enumerate = threadGroup.enumerate(list);
				for (int i = 0; i < enumerate; i++) {
					Thread t = list[i];
					if (t == currentThread) {
						continue;
					}
					t.interrupt();
				}
				for (int i = 0; i < enumerate; i++) {
					Thread t = list[i];
					if (t == currentThread) {
						continue;
					}
					try {
						t.join();
					} catch (InterruptedException e1) {
						LOGGER.trace("Interrupted", e1);
						break;
					}
				}
				activeCount = threadGroup.activeCount();
			}
		}
		return null;
	}
	
	/*
	 * Start - stop test
	 */
	public static void main(String[] args) throws InterruptedException {
		Thread t = new Thread(new ThreadGroup("a"), "b") {
			@Override
			public void run() {
				File file = new File(".");
				try {
					new ZooKeeperVestigeLauncher(file, file, file).call();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
		Thread.sleep(5000);
		t.interrupt();
		t.join();
	}

}
