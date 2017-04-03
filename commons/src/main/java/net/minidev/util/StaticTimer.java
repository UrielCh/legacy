package net.minidev.util;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Advance timer
 * 
 * @author Uriel Chemouni
 */
public class StaticTimer {
	private final static Timer timer;
	private final static ArrayList<TimerTask> tasks;

	static {
		timer = new Timer("Common-Timer", true);
		tasks = new ArrayList<TimerTask>();
		Runtime.getRuntime().addShutdownHook(new Thread(new Finalizer(), "TimerShutDownHook"));
	}

	public static void schedule(TimerTask task, long delay, long period) {
		timer.schedule(task, delay, period);
	}

	/**
	 * The schedule task Will Be Called a last Time on JVM Shutdown Event
	 */
	public static void scheduleCirical(TimerTask task, long delay, long period) {
		tasks.add(task);
		timer.schedule(task, delay, period);
	}

	private static class Finalizer implements Runnable {

		@Override
		public void run() {
			StaticTimer.lastRun();
		}
	}

	public static void lastRun() {
		timer.purge();
		timer.cancel();
		for (TimerTask task : tasks)
			task.run();
	}
}
