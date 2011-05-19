/**
 * 
 */
package com.github.elyast.lemac.core.internal;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.elyast.lemac.core.Activator;

/**
 * @author lukaszjastrzebski
 *
 */
public class CamelWrapper {

	private static final transient Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	public void start(ComponentContext cc, BundleContext bc, Map<?, ?> properties) {
		LOG.info("Hi I am called, {} {} {}", new Object[] { cc, bc, properties});
		listProps(properties);
	}

	private void listProps(Map<?, ?> properties) {
		for (Map.Entry<?,?> em : properties.entrySet()) {
			LOG.info("Property {}={}", em.getKey(), em.getValue());
		}
	}
	
	public void stop(ComponentContext cc, BundleContext bc, Map<?, ?> properties) {
		LOG.info("Hi I am stopped, {} {} {}", new Object[] { cc, bc, properties});
		listProps(properties);
	}
	
	public void modified(ComponentContext cc, BundleContext bc, Map<?, ?> properties) {
		LOG.info("Hi I am modified, {} {} {}", new Object[] { cc, bc, properties});
		listProps(properties);
	}
}
