package net.minidev.util;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.TreeSet;
import java.util.WeakHashMap;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Chronometre java Processing. Support MultiThreading.
 * 
 * @author uriel
 */
public class ChronoStat {
	private static WeakHashMap<Thread, ChronoStat> all = new WeakHashMap<Thread, ChronoStat>();
	private static TreeSet<String> knowStat = new TreeSet<String>();

	private static ChronoStat mainChrono = new ChronoStat();

	public static void dump(PrintStream out) {
		synchronized (knowStat) {
			for (String name : knowStat) {
				out.append(toString(name));
				out.print('\n');
			}
		}
	}

	public static ChronoData getChronoData(String name) {
		ChronoData data = new ChronoData();
		for (ChronoStat stat : all.values()) {
			Chrono c = stat.allChrono.get(name);
			if (c == null)
				continue;
			c.updateTime();
			data.add(c.data);
		}

		Chrono c = mainChrono.getChrono(name);
		if (c != null) {
			data.add(c.data);
		}
		return data;
	}

	public static String toString(String name) {
		StringBuilder sb = new StringBuilder();
		sb.append(name).append(' ');
		ChronoData data = getChronoData(name);
		NumberFormat nf = NumberFormat.getInstance();
		sb.append("Total: ").append(nf.format(data.timeTotal));
		sb.append("ms Moy:").append(nf.format(data.getAvg()));
		sb.append("ms NbOccur:").append(nf.format(data.poid));
		return sb.toString();
	}

	/**
	 * Get the chronostat from the Running Thread.
	 * 
	 * @return
	 */
	private static ChronoStat getChronoStat() {
		final Thread t = Thread.currentThread();
		ChronoStat stat = all.get(t);
		if (stat == null) {
			stat = new ChronoStat();
			all.put(t, stat);
		}
		return stat;
	}

	public static interface JMXViewMBean {
		public String getName();

		public long getCount();

		public long getAvg();

		public long getTotal();
	}

	static class JMXView implements JMXViewMBean {
		long lastUpdate = System.currentTimeMillis();
		String name;
		ChronoData data;

		public JMXView(String name) {
			this.name = name;
			MBeanServer server = null;
			ObjectName tBeanName = null;
			try {
				server = ManagementFactory.getPlatformMBeanServer();
				tBeanName = new ObjectName("ivi:type=ChronoStat,name=" + name);
				server.registerMBean(this, tBeanName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public long getCount() {
			update();
			return data.poid;
		}

		@Override
		public long getAvg() {
			update();
			return data.getAvg();
		}

		@Override
		public long getTotal() {
			update();
			return data.timeTotal;
		}

		void update() {
			if (data == null || System.currentTimeMillis() - lastUpdate > 300) {
				this.data = getChronoData(name);
			}
		}

	}

	public static void start(String name) {
		ChronoStat stat = getChronoStat();
		stat.startImp(name);
	}

	public static void stop(String name) {
		synchronized (knowStat) {
			try {
				if (!knowStat.contains(name)) {
					knowStat.add(name);
					new JMXView(name);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ChronoStat stat = getChronoStat();
		stat.stopImp(name);
	}

	public static void cancel(String name) {
		ChronoStat stat = getChronoStat();
		stat.cancelImp(name);
	}

	private Hashtable<String, Chrono> allChrono = new Hashtable<String, Chrono>();

	private Chrono getChrono(String name) {
		Chrono chrono = allChrono.get(name);
		if (chrono == null) {
			chrono = new Chrono();
			allChrono.put(name, chrono);
		}
		return chrono;
	}

	private void startImp(String name) {
		Chrono chrono = getChrono(name);
		chrono.start();
	}

	private void stopImp(String name) {
		Chrono chrono = getChrono(name);
		chrono.stop();
	}

	private void cancelImp(String name) {
		Chrono chrono = getChrono(name);
		chrono.cancel();
	}

	@Override
	protected void finalize() throws Throwable {
		mainChrono.importCs(this);
		super.finalize();
	}

	private void importCs(ChronoStat stat) {
		for (Map.Entry<String, Chrono> entry : stat.allChrono.entrySet()) {
			Chrono c = getChrono(entry.getKey());
			c.add(entry.getValue().data);
		}
	}

	private static class Chrono {
		boolean running = false;
		int pos = 0;
		long times[] = new long[20];

		ChronoData data = new ChronoData();

		public Chrono() {
		}

		public void start() {
			if (running) {
				// allready Running.
				return;
			}
			running = true;
			if (pos + 1 == times.length)
				updateTime();
			times[pos] = System.currentTimeMillis();
		}

		public void stop() {
			if (!running) {
				// not Running.
				return;
			}
			times[pos] = System.currentTimeMillis() - times[pos];
			running = false;
			pos++;
		}

		public void cancel() {
			if (!running) {
				// not Running.
				return;
			}
			times[pos] = 0;
			running = false;
		}

		private void add(ChronoData c) {
			this.data.add(c);
		}

		private void updateTime() {
			long n = 0;
			if (pos == 0)
				return;
			int limit = pos;
			if (running)
				limit--;
			for (int i = 0; i < pos; i++) {
				n += times[i];
			}
			data.poid += limit;
			data.timeTotal += n;
			if (running)
				times[0] = times[pos];
			pos = 0;
		}

		public String toString() {
			updateTime();
			return "Chono: " + NumberFormat.getInstance().format(data.timeTotal / data.poid) + "ms Nb occur:"
					+ data.poid;
		}
	}

	private static class ChronoData {
		public long timeTotal = 0;
		public long poid = 0;

		public void add(ChronoData data) {
			this.timeTotal += data.timeTotal;
			this.poid += data.poid;
		}

		public long getAvg() {
			if (poid == 0)
				return 0;
			return timeTotal / poid;
		}

	}

}
