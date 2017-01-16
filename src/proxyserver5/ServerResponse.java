/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package proxyserver5;

import java.util.Map;

/**
 *
 * @author ASUS
 */
public class ServerResponse {
    private String responseRaw;
    private Map<String, String> headers;

    public ServerResponse(String responseRaw, Map<String, String> headers) {
        setResponseRaw(responseRaw);
        setHeaders(headers);
    }

    public String getResponseRaw() {
        return responseRaw;
    }

    public void setResponseRaw(String responseRaw) {
        this.responseRaw = responseRaw;
    }

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void updateMaxAge(long expiration) {
		String cacheHeader = headers.get("Cache-Control");

		if (cacheHeader == null) {
			return;
		}

		String[] directives = cacheHeader.split(", ");

		for (int i=0;i<directives.length;++i) {
			String directive = directives[i];
			if (directive.matches("^max-age=[0-9]+")) {
				String[] parts = directive.split("=");
				long newMaxAge = (expiration - System.currentTimeMillis())/1000;
				parts[i] = "max-age="+newMaxAge;
				break;
			}
		}

		headers.put("Cache-Control", String.join(", ", directives));

		String[] headerAndBody = responseRaw.split("\r\n\r\n", 1);

		String body = headerAndBody[1];

		StringBuilder headerSb = new StringBuilder();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			headerSb.append(entry.getKey()+": "+entry.getValue());
		}

		responseRaw = headerSb.toString()+"\r\n\r\n"+body;
	}
}