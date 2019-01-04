package com.using.common.core.cache;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.using.common.core.cache.map.AbstractBaseCache;

public class MapCache extends AbstractBaseCache {
	
	private Logger logger = LoggerFactory.getLogger(MapCache.class);

	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();
	private Map<String, Long[]> timeMap = new ConcurrentHashMap<String, Long[]>();
	
	@PostConstruct
	public void init() {
		new ClearCacheThred(this).start();
	}

	public Object get(String key) {
		Long[] times = timeMap.get(key);
		if (times != null) {
			if (times[0] > System.currentTimeMillis()) {
				timeMap.put(key, new Long[]{System.currentTimeMillis() + times[1], times[1]});
				return map.get(key);
			} else {
				map.remove(key);
				timeMap.remove(key);
				return null;
			}
		} 
		
		return map.get(key);
	}

	public void put(String key, Object obj) {
		map.put(key, obj);
	}

	public void put(String key, Object obj, long timeout) {
		map.put(key, obj);
		timeMap.put(key, new Long[]{System.currentTimeMillis() + timeout * 1000, timeout * 1000});
	}

	public void remove(String key) {
		map.remove(key);
	}

	public Object get(String key, ICacheCaller caller, long expires) {
		Object o = null;
		if (key != null) {
			o = map.get(key);
			if (o == null) {
				o=caller.Call();
				map.put(key, o);
				if (expires > 0) {
					timeMap.put(key, new Long[]{System.currentTimeMillis() + expires * 1000, expires * 1000});
				}
				return o;
			} else {
				return o;
			}
		} else {
			return null;
		}
	}
	
	@Override
	public Object get(String key, ICacheCaller caller) {
		return get(key, caller, 0);
	}

	@Override
	public boolean replace(String key, Object obj) {
		map.put(key, obj);
		return true;
	}

	@Override
	public void removeKeyByPreTag(String preStr) {
		if (map != null) {
			Object[] keySet = map.keySet().toArray();
			for (int i = 0; i < keySet.length; i ++) {
				String key = (String) keySet[i];
				if (key.startsWith(preStr)) {
					map.remove(key);
				}
			}
		}
	}
	
	private void removeTimeoutCache() {
		for (Entry<String, Long[]> entry : timeMap.entrySet()) {
			if (entry.getValue()[0] < System.currentTimeMillis()) {
				logger.info(String.format("the key '%s' is timeout, clear it...", entry.getKey()));
				timeMap.remove(entry);
				map.remove(entry.getKey());
			}
		}
	}
	
	private static class ClearCacheThred extends Thread {
		private Logger logger = LoggerFactory.getLogger(ClearCacheThred.class);
		
		private MapCache mapCache;
		
		public ClearCacheThred(MapCache mapCache) {
			this.mapCache = mapCache;
		}
		
		public void run() {
			while(true) {
				try {
					Thread.sleep(EXPIRES_LEVEL_HOUR * 1000);
					//Thread.sleep(EXPIRES_LEVEL_HOUR);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info("It's time for clear cache of timeout, start now ...");
				mapCache.removeTimeoutCache();
				logger.info("clear cache of timeout complete.");
			}
		}
	}
}
