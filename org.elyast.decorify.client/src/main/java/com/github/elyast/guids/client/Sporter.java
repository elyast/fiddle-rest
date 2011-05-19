package com.github.elyast.guids.client;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/1/helloworld")
public class Sporter implements Sport {

	@Inject
	private Customizer plugin;
	@Override
	@GET
	@Path("/send")
	@Produces("text/plain")
	public String sayHello() {
		if (plugin == null) {
			return "String";
		}
		return plugin.getString("String");
	}

	void modified() {
		System.out.println("Modified: " + sayHello());
	}
	
	void start() {
		System.out.println("Start: " + sayHello());
		
	}
	
	void stop() {
		System.out.println("Stop: " + sayHello());
	}
}
