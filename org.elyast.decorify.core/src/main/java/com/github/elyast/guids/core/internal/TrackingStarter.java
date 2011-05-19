package com.github.elyast.guids.core.internal;

import java.util.Map;

import org.elyast.decorify.core.ServiceDecorator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;


public final class TrackingStarter {

	private final ServiceTracker<Object, Object> serviceTracker;
	private final BundleTracker<?> bundleTracker;

	public TrackingStarter(BundleContext context, ServiceDecorator decorator,
			Map<String, Object> properties) throws InvalidSyntaxException {
		ServiceDecoratorCustomizer customizer = new ServiceDecoratorCustomizer(
				context, decorator);
		serviceTracker = new ServiceTracker<Object, Object>(context,
				context.createFilter((String) properties.get("filter")),
				customizer);
		serviceTracker.open();
		bundleTracker = new BundleTracker<Object>(context, Bundle.STOPPING,
				customizer);
		bundleTracker.open();
	}

	public void close() {
		serviceTracker.close();
		bundleTracker.close();
	}

}
