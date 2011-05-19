/**
 * 
 */
package com.github.elyast.lemac.core.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.ExceptionMapper;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.BeanComponent;
import org.apache.camel.component.bean.BeanEndpoint;
import org.apache.camel.component.bean.BeanProcessor;
import org.apache.camel.component.cxf.cxfbean.CxfBeanComponent;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lukaszjastrzebski
 *
 */
public class RemoteExportListener implements ServiceListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(RemoteExportListener.class);

	private BundleContext context;
	private CamelContext camel;

	private SimpleRegistry registry;

	private HashMap<ServiceReference, String> idToServiceReferenceMapper = new HashMap<ServiceReference, String>();

	public RemoteExportListener(BundleContext context, CamelContext camel, SimpleRegistry registry) {
		this.context = context;
		this.camel = camel;
		this.registry = registry;
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
	 */
	@Override
	public void serviceChanged(ServiceEvent event) {
		switch(event.getType()) {
		case ServiceEvent.REGISTERED : register(event.getServiceReference()); break;
		case ServiceEvent.UNREGISTERING : unregister(event.getServiceReference());break;
		}

	}

	private void register(ServiceReference<?> serviceReference) {
		Object service = context.getService(serviceReference);
		LOG.info("Service {}, {}", service, serviceReference);
		try {
			registry.put("serviceBeansRef", service);
			registry.put("serviceBeansRef", service);
			ExceptionMapper<Throwable> mapper = new Kimono();
			registry.put("providers", Arrays.asList(new Object[] {mapper}));
			registry.put("charonExceptionMapper", mapper);
			CxfBeanComponent cxf = new CxfBeanComponent();
			cxf.setCamelContext(camel);
			final Endpoint x = cxf.createEndpoint("cxfbean:serviceBeansRef?providers=#charonExceptionMapper&setDefaultBus=true");
			
			camel.addRoutes(new RouteBuilder() {
				
				@Override
				public void configure() throws Exception {
					
					from("servlet:///1?matchOnUriPrefix=true").id("tasty").to(x);
					
				}
			});
			idToServiceReferenceMapper.put(serviceReference, "tasty");
		} catch (Exception e) {
			LOG.error("When registerint", e);
		}
	}

	private void unregister(ServiceReference<?> serviceReference) {
		// TODO Auto-generated method stub
		LOG.info("Service {}", serviceReference);
		String key = idToServiceReferenceMapper.get(serviceReference);
		try {
			camel.stopRoute(key);
			camel.removeRoute(key);
		} catch (Exception e) {
			LOG.error("When stopping", e);
		}
		
	}

}
