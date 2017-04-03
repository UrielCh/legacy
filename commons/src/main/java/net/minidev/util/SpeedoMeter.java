package net.minidev.util;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Save a per sec sliding speed
 * 
 * @author Uriel Chemouni
 * 
 */
public class SpeedoMeter {
	// private final static int MAX_AGE = 15 * 60; // 15 min Old Max
	private final static Map<String, SpeedBlock> map = new HashMap<String, SpeedBlock>();

	private final static int age1 = 60;
	private final static int age5 = 60*5;
	private final static int age15 = 60*15;

	public static void add(String name, int value) {
		SpeedBlock block = getBlock(name);
		long now = now();
		block.cleanOlder(now);
		block.add(value, now);
	}

	public static Speed getSpeed(String name) {
		SpeedBlock block = getBlock(name);
		return block.getSpeed();
	}

	public synchronized static SpeedBlock getBlock(String name) {
		SpeedBlock block = map.get(name);
		if (block == null) {
			// init
			block = new SpeedBlock(name);
			map.put(name, block);
		}
		return block;
	}

	public static interface SpeedBlockMBean {
		public float get1Min();

		public float get5Min();

		public float get15Min();
	}

	public static class SpeedBlock implements SpeedBlockMBean {
		String name;
		LinkedList<SpeedElm> elms;

		public SpeedBlock(String name) {
			this.name = name.replace(":", "");
			this.elms = new LinkedList<SpeedElm>();
			MBeanServer server = null;
			ObjectName tBeanName = null;
			try {
				server = ManagementFactory.getPlatformMBeanServer();
				tBeanName = new ObjectName("ivi:type=speed,name=" + name);
				server.registerMBean(this, tBeanName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public float get1Min() {
			return getSpeed()._1min;
		}

		public float get5Min() {
			return getSpeed()._5min;
		}

		public float get15Min() {
			return getSpeed()._15min;
		}

		public String toString() {
			return name + " " + getSpeed();
		}

		public void add(int value) {
			long now = now();
			add(value, now);
		}

		synchronized public void add(int value, long now) {
			SpeedElm lastElm = null;
			if (elms.size() > 0)
				lastElm = elms.getLast();

			if (lastElm != null && lastElm.time == now)
				lastElm.cnt += value;
			else {
				lastElm = new SpeedElm(value, now);
				elms.add(lastElm);
			}
		}

		synchronized public void cleanOlder(long now) {
			while (elms.size() > 0) {
				SpeedElm e = elms.getFirst();
				if (e.isToOld(now))
					elms.removeFirst();
				else
					break;
			}
		}

		synchronized public Speed getSpeed() {
			long now = now();
			cleanOlder(now);
			int _1 = 0;
			int _5 = 0;
			int _15 = 0;

			for (SpeedElm elm : elms) {
				int age = elm.getAge(now);
				if (age < age1) {
					_1 += elm.cnt;
					_5 += elm.cnt;
					_15 += elm.cnt;
				} else if (age < age5) {
					_5 += elm.cnt;
					_15 += elm.cnt;
				} else if (age < age15) {
					_15 += elm.cnt;
				}
			}
			Speed result = new Speed();
			result._15min = ((float) _15) / ((float) age15);
			result._5min = ((float) _5) / ((float) age5);
			result._1min = ((float) _1) / ((float) age1);
			return result;
		}
	}

	private static class SpeedElm {
		public long time; // in sec
		public int cnt;

		public SpeedElm(int value, long time) {
			this.cnt = value;
			this.time = time;
		}

		// public int getAge() {
		// return getAge(now());
		// }

		public int getAge(long now) {
			return (int) (now - time);
		}

		public boolean isToOld(long now) {
			return ((int) (now - time)) > age15;
		}
	}

	public static class Speed {
		public float _1min;
		public float _5min;
		public float _15min;

		public String toString() {
			return "(" + _1min + ", " + _5min + ", " + _15min + ")";
		}
	}

	private static long now() {
		return System.currentTimeMillis() / 1000L;
	}
}
