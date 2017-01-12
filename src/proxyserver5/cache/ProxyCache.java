package proxyserver5.cache;

import java.util.HashMap;
import java.util.Map;

import proxyserver5.ServerResponse;

public class ProxyCache {
	private Map<String, ServerResponse> eTagMap;
	private Map<ServerResponse, Long> expirationData;

	public ProxyCache() {
		eTagMap = new HashMap<>();
		expirationData = new HashMap<>();
	}
}