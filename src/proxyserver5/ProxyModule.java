package proxyserver5;

import javax.inject.Singleton;

import com.google.inject.AbstractModule;

public class ProxyModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ReadingUtil.class).in(Singleton.class);
	}
}
