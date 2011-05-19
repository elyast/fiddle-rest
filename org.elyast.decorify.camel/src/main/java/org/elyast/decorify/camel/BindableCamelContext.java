package org.elyast.decorify.camel;

import org.apache.camel.CamelContext;

public interface BindableCamelContext extends CamelContext {

	BindableRegistry getBindableRegistry();
}
