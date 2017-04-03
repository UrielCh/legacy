package net.minidev.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LocalUtils {
	public static int DefaultThreads = 8;
	private static ThreadPoolExecutor defaultEs;

	public synchronized static void execute(Runnable command) {
		if (defaultEs == null)
			defaultEs = newExecutor(DefaultThreads);
		defaultEs.execute(command);
	}

	public synchronized static void shutdown(int show) {
		if (defaultEs == null)
			return;
		shutdown(defaultEs, show);
		defaultEs = null;
	}

	public static ThreadPoolExecutor newExecutor(int nThreads) {
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
	}

	public static void shutdown(ExecutorService es, int show) {
		shutdown(es, show, "#done");
	}
	public static void shutdown(ExecutorService es, int show, String message) {
		if (es == null)
			return;
		if (!(es instanceof ThreadPoolExecutor))
			es.shutdown();
		int pass = 0;
		long startTime = -1;
		long startNb = -1;
		try {
			while (!es.awaitTermination(1, TimeUnit.SECONDS)) {
				if (es instanceof ThreadPoolExecutor) {
					ThreadPoolExecutor es2 = (ThreadPoolExecutor) es;
					long task = es2.getTaskCount();
					int actif = es2.getActiveCount();
					long done = es2.getCompletedTaskCount();

					if (startTime == -1) {
						startNb = done;
						startTime = System.currentTimeMillis();
					}

					if (show > 0 && ++pass % show == 0) {
						// System.gc();
						float deltaNb = done - startNb;
						// time in sec
						float detaTime = (System.currentTimeMillis() - startTime) / 1000F;

						// String pfix = "";
						float speed = -1;
						if (deltaNb > 0 && detaTime > 0) {
							speed = deltaNb / detaTime;
							// pfix = " Avg:" + (speed / 10) + "." + (speed % 10) + "/Sec";
						}
						System.out.printf("%s %d /%d Running:%d %.2f/Sec\r\n", message, done, task, actif, speed);
					}
					if (actif == 0)
						es.shutdown();
				} else {
				}
			}
		} catch (InterruptedException e) {
			System.err.println("receved: " + e);
		}
		es.shutdown();
	}

	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {
		}
	}
}
