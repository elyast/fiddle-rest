package org.elyast.decorify.camel;

import org.apache.camel.spi.Registry;

public interface BindableRegistry extends Registry {

	void bind(String s, Object o);
	
	void unbind(String paramString);
}
