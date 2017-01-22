package com.martin.httpproxy.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import com.martin.httpproxy.ServerResponse;
import com.martin.httpproxy.UserRequest;
import com.martin.httpproxy.cache.ProxyCache;

import static org.powermock.api.mockito.PowerMockito.*;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
public class ProxyCacheTest {

	private static final String QUERY = "google.com";

	@Mock
	private ServerResponse serverResponseMock;

	@Mock
	private UserRequest userRequestMock;

	@Before
	public void prepare() {
		when(userRequestMock.getMethod()).thenReturn("GET");
		when(userRequestMock.getQuery()).thenReturn(QUERY);
	}

	@Test
	public void shouldCacheByMaxAge() {
		ProxyCache toTest = new ProxyCache();

		Map<String, String> headers = getRegularHeaders();
		headers.put("Cache-Control", "public, max-age=360, no-transform");
		when(serverResponseMock.getHeaders()).thenReturn(headers);

		toTest.tryCache(serverResponseMock, QUERY);

		ServerResponse response = toTest.tryAnswerFromCache(userRequestMock);

		Assert.assertNotNull(response);
	}

	@Test
	public void shouldCacheByExpires() {
		ProxyCache toTest = new ProxyCache();

		int year = Year.now().getValue();

		Map<String, String> headers = getRegularHeaders();

		when(serverResponseMock.getHeaders()).thenReturn(headers);
		// Expires next year
		headers.put("Expires", "Thu, 01 Dec "+(year+1)+" 16:00:00 GMT");

		toTest.tryCache(serverResponseMock, QUERY);

		ServerResponse response = toTest.tryAnswerFromCache(userRequestMock);

		Assert.assertNotNull(response);
	}

	@Test
	public void shouldNotCachePrivateResponses() {
		ProxyCache toTest = new ProxyCache();

		Map<String, String> headers = getRegularHeaders();
		when(serverResponseMock.getHeaders()).thenReturn(headers);

		toTest.tryCache(serverResponseMock, QUERY);

		ServerResponse response = toTest.tryAnswerFromCache(userRequestMock);

		Assert.assertNull(response);
	}

	private Map<String, String> getRegularHeaders() {
		Map<String, String> headers = new HashMap<>();

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

		return headers;
	}
}