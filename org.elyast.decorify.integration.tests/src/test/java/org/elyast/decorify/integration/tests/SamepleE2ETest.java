package org.elyast.decorify.integration.tests;

import static org.junit.Assert.*;

import org.osgi.util.tracker.*;

import org.elyast.decorify.core.ServiceDecorator;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;

public class SamepleE2ETest {

	@Test
	public void test() throws InterruptedException, InvalidSyntaxException {
		BundleContext ctx = Activator.getContext();

		ServiceTracker<ServiceDecorator, Object> tracer = new ServiceTracker<ServiceDecorator, Object>(
				ctx, ServiceDecorator.class, null);
		tracer.open();

		tracer.waitForService(10000);

		assertTrue(tracer.getServices().length > 0);
	}

}
