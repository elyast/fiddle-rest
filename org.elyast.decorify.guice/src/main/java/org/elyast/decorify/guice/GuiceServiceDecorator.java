package org.elyast.decorify.guice;

import org.elyast.decorify.core.ServiceDecorator;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class GuiceServiceDecorator implements ServiceDecorator {

	private static final String BUNDLE_MODULE_NAME = "Bundle-DI-Context";
	private static final Logger LOGGER = LoggerFactory
			.getLogger(GuiceServiceDecorator.class);
	private Injector injector;

	@Override
	public void bundleStopped(Object decorationData) {
	}

	@Override
	public void serviceUnregistered(Object decorationData) {
	}

	@Override
	public boolean hookedOnBundleLifecycle() {
		return true;
	}

	@Override
	public Object serviceRegistered(ServiceReference<Object> reference,
			Object service) {
		injector.injectMembers(service);
		return null;
	}

	@Override
	public boolean hookedOnServiceLifecycle() {
		return false;
	}

	@Override
	public boolean match(ServiceReference<Object> reference, Object service) {
		Bundle bundle = reference.getBundle();
		return retrieveModuleClass(bundle) != null;
	}

	private Class<?> retrieveModuleClass(Bundle bundle) {
		try {
			String bundleModule = bundle.getHeaders().get(BUNDLE_MODULE_NAME);
			Class<?> moduleClass = bundle.loadClass(bundleModule);
			moduleClass.getConstructor();
			return moduleClass;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Object firstServiceRegisteredInBundle(
			ServiceReference<Object> reference, Object service) {
		try {
			Module module = (Module) retrieveModuleClass(reference.getBundle())
					.newInstance();
			injector = Guice.createInjector(module);
			return injector;
		} catch (Exception e) {
			LOGGER.warn("Exception while constructing decorating object");
			return null;
		}
	}

}
