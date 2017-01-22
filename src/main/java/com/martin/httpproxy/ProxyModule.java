package com.martin.httpproxy;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;
import com.martin.httpproxy.cache.Evictor;
import com.martin.httpproxy.cache.ProxyCache;

public class ProxyModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ReadingUtil.class).in(Singleton.class);
		bind(ProxyCache.class).in(Singleton.class);
		bind(Evictor.class).in(Singleton.class);
	}
}
