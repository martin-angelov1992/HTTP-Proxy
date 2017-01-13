package proxyserver5.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import proxyserver5.ServerResponse;

public class Evictor {
	private ProxyCache cache;

	private static final long EVICTION_INTERVAL = 2 * 60 * 1000; // Every 2
																	// minutes
	private static final long STALING_TIME = 10 * 60 * 1000; // if response is
																// not used in
																// 10 minutes,
																// it get's
																// removed from
																// cache

	private void evict() {
		evictETags();
		evictMaxAge();
	}

	private void evictETags() {
		Map<String, ServerReponseData> eTagMap = cache.getETagMap();

		Iterator<ServerReponseData> it = eTagMap.values().iterator();
		while (it.hasNext()) {
			ServerReponseData data = it.next();

			if (data.getLastUsage() + STALING_TIME < System.currentTimeMillis()) {
				it.remove();
			}
		}
	}

	private void evictMaxAge() {
		Map<String, MaxAgeCacheData> expDataMap = cache.getExpirationData();

		Iterator<MaxAgeCacheData> it = expDataMap.values().iterator();
		while (it.hasNext()) {
			MaxAgeCacheData data = it.next();

			if (data.getLastUsage() + STALING_TIME < System.currentTimeMillis() || 
					data.getExpiration() > System.currentTimeMillis()) {
				it.remove();
			}
		}
	}

	public void start() {
		Timer time = new Timer();
		TimerTask scheduler = new EvictionScheduler();
		time.schedule(scheduler, EVICTION_INTERVAL, EVICTION_INTERVAL); // Create
																		// Repetitively
																		// task
																		// for
																		// every
																		// X
																		// secs
	}

	private class EvictionScheduler extends TimerTask {

		@Override
		public void run() {
			evict();
		}
	}
}