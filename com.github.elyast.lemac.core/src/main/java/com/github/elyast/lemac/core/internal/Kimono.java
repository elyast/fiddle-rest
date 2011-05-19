package com.github.elyast.lemac.core.internal;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Kimono implements ExceptionMapper<Throwable> {
	
	private static final Logger LOG = LoggerFactory.getLogger(Kimono.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		LOG.error("exception", exception);
		return Response.serverError().build();
	}
	
	@Override
	public String toString() {
		return "charonExceptionMapper";
	}
}