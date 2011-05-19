package org.elyast.decorify.core;

import org.osgi.framework.ServiceReference;

public interface ServiceDecorator {

	void bundleStopped(Object decorationData);

	void serviceUnregistered(Object decorationData);

	boolean hookedOnBundleLifecycle();

	Object serviceRegistered(ServiceReference<Object> reference, Object service);

	boolean hookedOnServiceLifecycle();

	boolean match(ServiceReference<Object> reference, Object service);

	Object firstServiceRegisteredInBundle(ServiceReference<Object> reference,
			Object service);

}
