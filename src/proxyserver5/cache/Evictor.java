package proxyserver5.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Evictor {
	static Logger logger = LoggerFactory.getLogger(Evictor.class.getName());
	@Inject
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
		evictMaxAge();
	}

	private void evictMaxAge() {
		Map<String, MaxAgeCacheData> expDataMap = cache.getExpirationData();

		expDataMap.entrySet()
		Iterator<MaxAgeCacheData> it = expDataMap.values().iterator();
		while (it.hasNext()) {
			MaxAgeCacheData data = it.next();

			if (data.getLastUsage() + STALING_TIME < System.currentTimeMillis() || 
					data.getExpiration() < System.currentTimeMillis()) {
				it.remove();
			}
		}
	}

	public void start() {
		logger.debug("Evictor started.");
		Timer timer = new Timer();
		TimerTask scheduler = new EvictionScheduler();
		timer.schedule(scheduler, EVICTION_INTERVAL, EVICTION_INTERVAL); // Create
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
			logger.debug("Eviction time.");
			evict();
		}
	}
}