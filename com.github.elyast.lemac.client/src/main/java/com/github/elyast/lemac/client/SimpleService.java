package com.github.elyast.lemac.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/1/helloworld")
public class SimpleService implements SimpleSimpleService {

	@GET
	@Path("/send")
	@Produces("text/plain")
	public String say() {
		return "message";

	}
}
