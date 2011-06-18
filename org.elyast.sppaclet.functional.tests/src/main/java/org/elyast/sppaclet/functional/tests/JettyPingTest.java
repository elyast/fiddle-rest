package org.elyast.sppaclet.functional.tests;

import static org.junit.Assert.*;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;

public class JettyPingTest {

	public void shouldResponseWith404_WhenAskForSslUrl() throws Exception {
		System.out.println("shouldResponseWith404_WhenAskForSslUrl");
		HttpClient client = wrapClient(new DefaultHttpClient());
		HttpGet httpget = new HttpGet("https://localhost:8443/Something");

		// when
		HttpResponse resp = client.execute(httpget);

		// then
		assertEquals(404, resp.getStatusLine().getStatusCode());
	}

	@Test
	public void shouldResponseWith404_WhenAskForHttpUrl() throws Exception {
		System.out.println("shouldResponseWith404_WhenAskForHttpUrl");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet("http://localhost:8080/Something");

		// when
		HttpResponse resp = client.execute(httpget);

		// then
		assertEquals(404, resp.getStatusLine().getStatusCode());
	}

	@SuppressWarnings("deprecation")
	public static HttpClient wrapClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs,
						String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager ccm = base.getConnectionManager();
			SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new Scheme("https", ssf, 443));
			return new DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
