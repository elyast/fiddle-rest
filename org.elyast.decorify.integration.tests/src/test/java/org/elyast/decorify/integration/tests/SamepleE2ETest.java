package org.elyast.decorify.integration.tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.osgi.util.tracker.*;

import org.elyast.decorify.core.ServiceDecorator;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public class SamepleE2ETest {

	@Test
	public void test() throws InterruptedException, InvalidSyntaxException {
		BundleContext ctx = Activator.getContext();
		
		ServiceTracker tracer = new ServiceTracker(ctx, ServiceDecorator.class, null); 
		tracer.open();
		
		tracer.waitForService(10000);
		ServiceReference[] ref=ctx.getAllServiceReferences(null, null);
		for (ServiceReference serviceReference : ref) {
			System.out.println(serviceReference);
		}
		assertTrue(tracer.getServices().length > 0);
	}

}
