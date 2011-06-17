package org.elyast.sppaclet.functional.tests;

import static org.junit.Assert.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class JettyPingTest {

	@Test
	public void shouldResponseWith404_WhenAskForSslUrl() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("https://localhost:8443/Something");
		
		//when
		HttpResponse resp = client.execute(httpget);
		
		//then
		assertEquals(404, resp.getStatusLine().getStatusCode());
	}

	
	@Test
	public void shouldResponseWith404_WhenAskForHttpUrl() throws Exception {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("https://localhost:8080/Something");
		
		//when
		HttpResponse resp = client.execute(httpget);
		
		//then
		assertEquals(404, resp.getStatusLine().getStatusCode());
	}
	
}
