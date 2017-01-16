package proxyserver5;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

import proxyserver5.cache.Evictor;
import proxyserver5.cache.ProxyCache;

public class ProxyModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ReadingUtil.class).in(Singleton.class);
		bind(ProxyCache.class).in(Singleton.class);
		bind(Evictor.class).in(Singleton.class);
	}
}
