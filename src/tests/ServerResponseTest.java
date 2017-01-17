package tests;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import proxyserver5.ServerResponse;

@RunWith(PowerMockRunner.class)
public class ServerResponseTest {

	@Test
	public void shouldUpdateMaxAge() {
		String responseRaw = "HTTP/1.1 200 OK\r\n"+
				"Host: net.tutsplus.com\r\n"+
				"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\r\n"+
				"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
				"Accept-Language: en-us,en;q=0.5\r\n"+
				"Accept-Encoding: gzip,deflate\r\n"+
				"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
				"Keep-Alive: 300\r\n"+
				"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
				"Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\r\n"+
				"Pragma: no-cache\r\n"+
				"Cache-Control: public, max-age=360, no-transform\r\n\r\n"+
				"<html><body><h1>Hello, World!</h1></body></html>";

		Map<String, String> headers = new LinkedHashMap<>();

		headers.put("Host", "net.tutsplus.com");
		headers.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)");
		headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.put("Accept-Language", "en-us,en;q=0.5");
		headers.put("Accept-Encoding", "gzip,deflate");
		headers.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		headers.put("Keep-Alive", "300");
		headers.put("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		headers.put("Cookie", "PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120");
		headers.put("Pragma", "no-cache");
		headers.put("Cache-Control", "public, max-age=360, no-transform");

		ServerResponse toTest = new ServerResponse(responseRaw, headers, "HTTP/1.1 200 OK");

		toTest.updateMaxAge(30);

		Assert.assertEquals("HTTP/1.1 200 OK\r\n"+
							"Host: net.tutsplus.com\r\n"+
							"User-Agent: Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.5) Gecko/20091102 Firefox/3.5.5 (.NET CLR 3.5.30729)\r\n"+
							"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"+
							"Accept-Language: en-us,en;q=0.5\r\n"+
							"Accept-Encoding: gzip,deflate\r\n"+
							"Accept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\n"+
							"Keep-Alive: 300\r\n"+
							"Cookie: PHPSESSID=r2t5uvjq435r4q7ib3vtdjq120\r\n"+
							"Pragma: no-cache\r\n"+
							"Cache-Control: public, max-age=30, no-transform\r\n\r\n"+
							"<html><body><h1>Hello, World!</h1></body></html>", toTest.getResponseRaw());

		Assert.assertEquals("public, max-age=30, no-transform", toTest.getHeaders().get("Cache-Control"));
	}
}