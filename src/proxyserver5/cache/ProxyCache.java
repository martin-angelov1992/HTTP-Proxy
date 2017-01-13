package proxyserver5.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import proxyserver5.ServerResponse;
import proxyserver5.UserRequest;

public class ProxyCache {
	private Map<String, MaxAgeCacheData> expirationData;

	public ProxyCache() {
		expirationData = new ConcurrentHashMap<>();
	}

	Map<String, MaxAgeCacheData> getExpirationData() {
		return expirationData;
	}

	public void tryCache(ServerResponse serverResponse) {
		tryCacheByMaxAge(serverResponse);
	}

	private void tryCacheByMaxAge(ServerResponse serverResponse) {
		serverResponse.getHeaders().get(key)
		MaxAgeCacheData data = new MaxAgeCacheData(System.currentTimeMillis());
		expirationData.put(key, value);
	}

	public ServerResponse tryAnswerFromCache(UserRequest request) {
		return tryAnswerByMaxAge(request);
	}

	private ServerResponse tryAnswerByMaxAge(UserRequest request) {
		// Only get requests are supported for caching
		if (!"GET".equals(request.getMethod())) {
			return null;
		}

		MaxAgeCacheData data = expirationData.get(request.getQuery());

		if (data.getExpiration() > System.currentTimeMillis()) {
			expirationData.remove(request.getQuery());
			return null;
		}

		// We need to send response with the remaining time
		data.getServerResponse().updateMaxAge(data.getExpiration());
		return data.getServerResponse();
	}
}