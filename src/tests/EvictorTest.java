package tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.support.membermodification.MemberModifier;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import proxyserver5.cache.Evictor;
import proxyserver5.cache.MaxAgeCacheData;
import proxyserver5.cache.ProxyCache;

import static org.powermock.api.mockito.PowerMockito.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Timer.class, Evictor.class})
public class EvictorTest {
	private static final int ONE_MINUTE_IN_MS = 60 * 1000;

	@Mock
	private Timer mockTimer;

	private ProxyCache cache = new ProxyCache();

	@Mock
	private MaxAgeCacheData expiredData;

	@Mock
	private MaxAgeCacheData notExpiredData;

	@Mock
	private MaxAgeCacheData unusedData;

	@Test
	public void shouldEvictOldEntry() throws Exception {
		// Expired 10 minutes ago
		when(expiredData.getExpiration()).thenReturn(System.currentTimeMillis() - 10 * ONE_MINUTE_IN_MS);
		// But used 5 seconds ago. Should not happen anyway
		when(expiredData.getLastUsage()).thenReturn(System.currentTimeMillis() - 5000);

		// Expires after 10 minutes
		when(notExpiredData.getExpiration()).thenReturn(System.currentTimeMillis() + 10 * ONE_MINUTE_IN_MS);
		// Used 5 seconds ago
		when(notExpiredData.getLastUsage()).thenReturn(System.currentTimeMillis() - 5000);

		// Expires in 10 minutes
		when(unusedData.getExpiration()).thenReturn(System.currentTimeMillis() + 10 * ONE_MINUTE_IN_MS);
		// But not used for 1 day
		when(unusedData.getLastUsage()).thenReturn(System.currentTimeMillis() - 24 * 60 * ONE_MINUTE_IN_MS);

		whenNew(Timer.class).withNoArguments().thenReturn(mockTimer);
		Answer<Void> ans = new Answer<Void>() {
			public Void answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				TimerTask task = (TimerTask) args[0];
				task.run();
				return null;
			}
		};
		doAnswer(ans).when(mockTimer).schedule(any(), anyLong(), anyLong());

		Evictor toTest = new Evictor();

		MemberModifier.field(Evictor.class, "cache").set(
				toTest, cache);

		Map<String, MaxAgeCacheData> dataMap = new HashMap<>();
		dataMap.put("google.com", notExpiredData);
		dataMap.put("youtube.com", unusedData);
		dataMap.put("yahoo.com", expiredData);

		MemberModifier.field(ProxyCache.class, "expirationData").set(
				cache, dataMap);

		toTest.start();

		Assert.assertEquals(dataMap.size(), 1);
		Assert.assertEquals(dataMap.get("google.com"), notExpiredData);
	}
}