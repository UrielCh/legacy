package net.minidev.net;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;

/*
 This code is public domain: you are free to use, link and/or modify it in any way you want, for all purposes including commercial applications.
 http://javaskeleton.blogspot.com/2010/07/avoiding-peer-not-authenticated-with.html 
 */
public class WebClientDevWrapper {
	@SuppressWarnings("deprecation")
	public static org.apache.http.impl.client.DefaultHttpClient wrapClient(HttpClient base) {
		try {
			// SSLContext ctx = SSLContext.getInstance("TLS");
			SSLContext ctx = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);
			org.apache.http.conn.ssl.SSLSocketFactory ssf = new org.apache.http.conn.ssl.SSLSocketFactory(ctx);
			ssf.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			org.apache.http.conn.ClientConnectionManager ccm = base.getConnectionManager();
			org.apache.http.conn.scheme.SchemeRegistry sr = ccm.getSchemeRegistry();
			sr.register(new org.apache.http.conn.scheme.Scheme("https", ssf, 443));
			return new org.apache.http.impl.client.DefaultHttpClient(ccm, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}