package org.elyast.decorify.http;

import java.util.UUID;

import javax.ws.rs.Path;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cxf.cxfbean.CxfBeanComponent;
import org.elyast.decorify.camel.BindableCamelContext;
import org.elyast.decorify.camel.BindableRegistry;
import org.elyast.decorify.core.ServiceDecorator;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestServicesDecorator implements ServiceDecorator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestServicesDecorator.class);
	private BindableCamelContext camel;

	void bind(BindableCamelContext camel) {
		this.camel = camel;
	}

	void unbind(BindableCamelContext camel) {
		this.camel = null;
	}

	@Override
	public void bundleStopped(Object decorationData) {
	}

	@Override
	public void serviceUnregistered(Object decorationData) {
		LOGGER.info("Unregistering remote service.. {}", decorationData);
		try {
			String key = (String)decorationData;
			camel.stopRoute(key);
			camel.removeRoute(key);
			BindableRegistry registry = camel.getBindableRegistry();
			registry.unbind("serviceBeansRef");
		} catch (Exception e) {
			LOGGER.error("When stopping camel route", e);
		}
	}

	@Override
	public boolean hookedOnBundleLifecycle() {
		return false;
	}

	@Override
	public Object serviceRegistered(ServiceReference<Object> reference,
			Object service) {
		try {
			LOGGER.info("Registering remote service.. {}", reference);
			BindableRegistry registry = camel.getBindableRegistry();
			registry.bind("serviceBeansRef", service);

			CxfBeanComponent cxf = new CxfBeanComponent();
			cxf.setCamelContext(camel);
			final Endpoint x = cxf
					.createEndpoint("cxfbean:serviceBeansRef");
			final String id = UUID.randomUUID().toString();
			final String prefix = service.getClass().getAnnotation(Path.class).value();

			camel.addRoutes(new RouteBuilder() {

				@Override
				public void configure() throws Exception {

					from("servlet://" + prefix + "?matchOnUriPrefix=true").id(id)
							.to(x);

				}
			});
			return id;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public boolean hookedOnServiceLifecycle() {
		return true;
	}

	@Override
	public boolean match(ServiceReference<Object> reference, Object service) {
		return true;
	}

	@Override
	public Object firstServiceRegisteredInBundle(
			ServiceReference<Object> reference, Object service) {
		return null;
	}
}
