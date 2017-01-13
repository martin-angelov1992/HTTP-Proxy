package proxyserver5.cache;

import proxyserver5.ServerResponse;

public class ServerReponseData {
	private ServerResponse serverResponse;
	private long lastUsage;

	public ServerReponseData(ServerResponse serverResponse, long lastUsage) {
		super();
		this.serverResponse = serverResponse;
		this.lastUsage = lastUsage;
	}

	public ServerResponse getServerResponse() {
		return serverResponse;
	}

	public long getLastUsage() {
		return lastUsage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (lastUsage ^ (lastUsage >>> 32));
		result = prime * result + ((serverResponse == null) ? 0 : serverResponse.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerReponseData other = (ServerReponseData) obj;
		if (lastUsage != other.lastUsage)
			return false;
		if (serverResponse == null) {
			if (other.serverResponse != null)
				return false;
		} else if (!serverResponse.equals(other.serverResponse))
			return false;
		return true;
	}
}