package com.martin.httpproxy.cache;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martin.httpproxy.ServerResponse;
import com.martin.httpproxy.UserRequest;

public class ProxyCache {
	private Map<String, MaxAgeCacheData> expirationData;
	private SimpleDateFormat headerFormatParser = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
	static Logger logger = LoggerFactory.getLogger(ProxyCache.class.getName());

	public ProxyCache() {
		expirationData = new ConcurrentHashMap<>();
	}

	Map<String, MaxAgeCacheData> getExpirationData() {
		return expirationData;
	}

	public void tryCache(ServerResponse serverResponse, String URL) {
		CacheByMaxAgeResult result = tryCacheByMaxAge(serverResponse, URL);

		if (result == CacheByMaxAgeResult.CACHED || result == CacheByMaxAgeResult.SHOULD_NOT_CACHE) {
			return;
		}

		tryCacheByExpires(serverResponse, URL);
	}

	private void tryCacheByExpires(ServerResponse serverResponse, String URL) {
		String expires = serverResponse.getHeaders().get("Expires");

		if (expires == null) {
			return;
		}

		try {
			Date date = headerFormatParser.parse(expires);
			MaxAgeCacheData data = new MaxAgeCacheData(System.currentTimeMillis(), date.getTime(), serverResponse);
			logger.debug("Caching {}", URL);
			expirationData.put(URL, data);
		} catch (ParseException e) {
			logger.error("Unable to parse {}", expires);
		}
	}

	private CacheByMaxAgeResult tryCacheByMaxAge(ServerResponse serverResponse, String URL) {
		String cacheControlHeader = serverResponse.getHeaders().get("Cache-Control");

		if (cacheControlHeader == null) {
			return CacheByMaxAgeResult.UNABLE_TO_CACHE;
		}

		String[] directives = cacheControlHeader.split(", ");

		long age = -1;
		boolean isPublic = false;

		for (String directive : directives) {
			if (directive.matches("^max-age=[0-9]+$")) {
				String[] parts = directive.split("=");
				age = Long.valueOf(parts[1]);
			} else if (directive.matches("^private") || directive.matches("^no-cache")
					|| directive.matches("^no-store")) {
				return CacheByMaxAgeResult.SHOULD_NOT_CACHE;
			} else if (directive.matches("public")) {
				isPublic = true;
			}
		}

		if (!isPublic) {
			return CacheByMaxAgeResult.SHOULD_NOT_CACHE;
		}

		if (age <= 0) {
			return CacheByMaxAgeResult.UNABLE_TO_CACHE;
		}

		MaxAgeCacheData data = new MaxAgeCacheData(System.currentTimeMillis(), System.currentTimeMillis() + age * 1000,
				serverResponse);
		logger.debug("Caching {}", URL);
		expirationData.put(URL, data);
		return CacheByMaxAgeResult.CACHED;
	}

	public ServerResponse tryAnswerFromCache(UserRequest request) {
		// Only get requests are supported for caching
		if (!"GET".equals(request.getMethod())) {
			return null;
		}

		MaxAgeCacheData data = expirationData.get(request.getQuery());

		if (data == null) {
			return null;
		}

		if (data.getExpiration() < System.currentTimeMillis()) {
			logger.debug("Evicting {}", request.getQuery());
			expirationData.remove(request.getQuery());
			return null;
		}

		// We need to send response with the remaining time
		data.getServerResponse().updateMaxAge((data.getExpiration() - System.currentTimeMillis()) / 1000);
		logger.debug("Returning cached response for {}", request.getQuery());
		return data.getServerResponse();
	}

	private enum CacheByMaxAgeResult {
		UNABLE_TO_CACHE, SHOULD_NOT_CACHE, CACHED
	}
}