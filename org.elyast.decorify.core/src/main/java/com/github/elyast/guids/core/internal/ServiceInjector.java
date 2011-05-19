package com.github.elyast.guids.core.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.elyast.decorify.core.ServiceDecorator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServiceInjector {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceInjector.class);

	private final ConcurrentMap<ServiceReference<ServiceDecorator>, TrackingStarter> decorations = new ConcurrentHashMap<ServiceReference<ServiceDecorator>, TrackingStarter>();

	void register(ServiceReference<ServiceDecorator> decoratorRef) {
		try {
			LOGGER.info("Registering decorator...{}", decoratorRef);
			BundleContext context = decoratorRef.getBundle().getBundleContext();
			ServiceDecorator decorator = context.getService(decoratorRef);
			decorations.put(decoratorRef, new TrackingStarter(context,
					decorator, retrieveProperties(decoratorRef)));
		} catch (InvalidSyntaxException e) {
			LOGGER.warn("Cannot decorate with {}, exception occured {}",
					decoratorRef, e.getMessage());
		}
	}

	private Map<String, Object> retrieveProperties(
			ServiceReference<ServiceDecorator> decoratorRef) {
		Map<String, Object> props = new HashMap<String, Object>();
		String[] keys = decoratorRef.getPropertyKeys();
		for (String string : keys) {
			props.put(string, decoratorRef.getProperty(string));
		}
		return props;
	}

	void unregister(ServiceReference<ServiceDecorator> decoratorRef) {
		LOGGER.info("Unregistering decorator...{}", decoratorRef);
		TrackingStarter tracker = decorations.remove(decoratorRef);
		if (tracker == null) {
			return;
		}
		tracker.close();
		decoratorRef.getBundle().getBundleContext().ungetService(decoratorRef);
	}

	void stop() {
		for ( TrackingStarter tracker : decorations.values()) {
			tracker.close();
		}
		decorations.clear();
	}
	
}
