package com.github.elyast.guids.core.internal;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.elyast.decorify.core.ServiceDecorator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceDecoratorCustomizer implements
		ServiceTrackerCustomizer<Object, Object>,
		BundleTrackerCustomizer<Object> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceDecoratorCustomizer.class);
	private final BundleContext context;
	private final ConcurrentMap<Bundle, Object> bundleCache = new ConcurrentHashMap<Bundle, Object>();
	private final ConcurrentMap<ServiceReference<Object>, Object> serviceCache = new ConcurrentHashMap<ServiceReference<Object>, Object>();
	private final ServiceDecorator decorator;

	public ServiceDecoratorCustomizer(BundleContext context,
			ServiceDecorator decorator) {
		this.context = context;
		this.decorator = decorator;
	}

	@Override
	public Object addingService(ServiceReference<Object> reference) {
		Object service = context.getService(reference);
		if (!decorator.match(reference, service)) {
			return null;
		}
		LOGGER.info("Decorating service {}", reference);
		if (decorator.hookedOnBundleLifecycle()) {
			Bundle bundle = reference.getBundle();
			synchronized (bundleCache) {
				Object data = null;
				if (!bundleCache.containsKey(bundle)) {
					data = decorator.firstServiceRegisteredInBundle(reference, service);
					LOGGER.info("Registering data {} for {}", data, bundle);
					bundleCache.put(bundle, data);
				} else {
					data = bundleCache.get(bundle);
				}
				decorator.serviceRegistered(reference, service);
			}
		}

		if (decorator.hookedOnServiceLifecycle()) {
			Object data = decorator.serviceRegistered(reference, service);
			LOGGER.info("Registering data {} for {}", data, reference);
			serviceCache.put(reference, data);
		}
		return service;
	}

	@Override
	public void modifiedService(ServiceReference<Object> reference,
			Object service) {

	}

	@Override
	public void removedService(ServiceReference<Object> reference,
			Object service) {
		if (decorator.hookedOnServiceLifecycle()) {
			Object decorationData = serviceCache.remove(reference);
			decorator.serviceUnregistered(decorationData);
		}
		context.ungetService(reference);
	}

	@Override
	public Bundle addingBundle(Bundle bundle, BundleEvent event) {
		return bundle;
	}

	@Override
	public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		if (!decorator.hookedOnBundleLifecycle()) {
			return;
		}
		Object decorationData = bundleCache.remove(bundle);
		decorator.bundleStopped(decorationData);
	}

}
