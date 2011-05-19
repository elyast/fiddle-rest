package org.elyast.decorify.camel.internal;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.camel.impl.JndiRegistry;
import org.elyast.decorify.camel.BindableRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BindableJndiRegistry extends JndiRegistry implements
		BindableRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(BindableJndiRegistry.class);
	
	public BindableJndiRegistry() {
		super();
	}

	public BindableJndiRegistry(Context context) {
		super(context);
	}

	@Override
	public void unbind(String paramString) {
		try {
			getContext().unbind(paramString);
		} catch (NamingException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
