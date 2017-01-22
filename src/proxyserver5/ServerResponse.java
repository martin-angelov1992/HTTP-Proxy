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
    private String firstLine;
    private char code;

    public ServerResponse(char code, String responseRaw, Map<String, String> headers, String firstLine) {
        setResponseRaw(responseRaw);
        setHeaders(headers);
        setFirstLine(firstLine);
        this.code = code;
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

	public String getFirstLine() {
		return firstLine;
	}

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	public char getCode() {
		return code;
	}

	public void updateMaxAge(long newMaxAge) {
		String cacheHeader = headers.get("Cache-Control");

		if (cacheHeader == null) {
			return;
		}

		String[] directives = cacheHeader.split(", ");

		for (int i=0;i<directives.length;++i) {
			String directive = directives[i];
			if (directive.matches("^max-age=[0-9]+")) {
				directives[i] = "max-age="+newMaxAge;
				break;
			}
		}

		headers.put("Cache-Control", String.join(", ", directives));

		String[] headerAndBody = responseRaw.split("\r\n\r\n", 2);

		String body = headerAndBody[1];

		StringBuilder headerSb = new StringBuilder();

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			headerSb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
		}

		responseRaw = firstLine+headerSb.toString()+"\r\n"+body;
	}
}