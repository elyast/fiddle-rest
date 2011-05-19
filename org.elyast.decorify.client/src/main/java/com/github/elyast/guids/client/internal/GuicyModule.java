package com.github.elyast.guids.client.internal;

import com.github.elyast.guids.client.Customizer;
import com.google.inject.AbstractModule;

public class GuicyModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(Customizer.class).to(SportCustomizer.class);
	}

}
