package com.martin.httpproxy.cache;

import com.martin.httpproxy.ServerResponse;

public class MaxAgeCacheData {
	private long lastUsage;
	private long expiration;
	private ServerResponse serverResponse;

	public MaxAgeCacheData(long lastUsage, long expiration, ServerResponse serverResponse) {
		super();
		this.lastUsage = lastUsage;
		this.expiration = expiration;
		this.serverResponse = serverResponse;
	}
	public long getLastUsage() {
		return lastUsage;
	}
	public void setLastUsage(long lastUsage) {
		this.lastUsage = lastUsage;
	}
	public long getExpiration() {
		return expiration;
	}
	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}
	public ServerResponse getServerResponse() {
		return serverResponse;
	}
	public void setServerResponse(ServerResponse serverResponse) {
		this.serverResponse = serverResponse;
	}
}