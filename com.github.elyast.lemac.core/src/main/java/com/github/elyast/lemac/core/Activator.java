package com.github.elyast.lemac.core;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.camel.CamelContext;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.camel.core.osgi.OsgiDefaultCamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.elyast.lemac.core.internal.RemoteExportListener;

public class Activator implements BundleActivator {

	private static final transient Logger LOG = LoggerFactory.getLogger(Activator.class);
	
	private BundleContext context;
	private RemoteExportListener exportListener;
	/**
	 * HttpService reference.
	 */
	private ServiceReference<?> httpServiceRef;
	private static boolean registerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		context = bundleContext;
		SimpleRegistry registry = new SimpleRegistry();
		CamelContext camel = new OsgiDefaultCamelContext(bundleContext, registry );
		camel.start();
		registerServlet(bundleContext);
		exportListener = new RemoteExportListener(context, camel, registry);
		context.addServiceListener(exportListener,
				"(service.exported.interfaces=*)");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		context.removeServiceListener(exportListener);
		context = null;
		exportListener = null;
		if (httpServiceRef != null) {
			bundleContext.ungetService(httpServiceRef);
			httpServiceRef = null;
		}
	}

	protected void registerServlet(BundleContext bundleContext)
			throws Exception {
		httpServiceRef = bundleContext.getServiceReference(HttpService.class
				.getName());

		if (httpServiceRef != null && !registerService) {
			LOG.info("Register the servlet service");
			final HttpService httpService = (HttpService) bundleContext
					.getService(httpServiceRef);
			if (httpService != null) {
				// create a default context to share between registrations
				final HttpContext httpContext = httpService
						.createDefaultHttpContext();
				// register the hello world servlet
				final Dictionary<String, String> initParams = new Hashtable<String, String>();
				initParams.put("matchOnUriPrefix", "false");
				initParams.put("servlet-name", "CamelServlet");
				httpService.registerServlet("/camel/services", // alias
						new CamelHttpTransportServlet(), // register servlet
						initParams, // init params
						httpContext // http context
						);
				registerService = true;
			}
		}
	}

}
