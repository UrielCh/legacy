package net.minidev.net;

import java.net.InetAddress;
import java.util.HashMap;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.UnsupportedSchemeException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/**
 * @see DefaultRoutePlanner
 * @author uriel
 * 
 */
public class RoutePlannerRedirect implements HttpRoutePlanner {
	private HashMap<String, InetAddress> remap = new HashMap<String, InetAddress>();

	public void map(String hostname, InetAddress addr) {
		remap.put(hostname, addr);
	}

	private final SchemePortResolver schemePortResolver;

	public RoutePlannerRedirect(final SchemePortResolver schemePortResolver) {
		super();
		this.schemePortResolver = schemePortResolver != null ? schemePortResolver : DefaultSchemePortResolver.INSTANCE;
	}

	public HttpRoute determineRoute(final HttpHost host, final HttpRequest request, final HttpContext context) throws HttpException {
		Args.notNull(host, "Target host");
		Args.notNull(request, "Request");
		final HttpClientContext clientContext = HttpClientContext.adapt(context);
		final RequestConfig config = clientContext.getRequestConfig();
		final InetAddress local = config.getLocalAddress();
		HttpHost proxy = config.getProxy();
		if (proxy == null) {
			proxy = determineProxy(host, request, context);
		}

		final HttpHost target;
		if (host.getPort() <= 0) {
			try {
				int port = this.schemePortResolver.resolve(host);
				String hostname = host.getHostName();
				InetAddress addr = remap.get(hostname);
				if (addr == null)
					target = new HttpHost(hostname, port, host.getSchemeName());
				else
					target = new HttpHost(addr, port, host.getSchemeName());
			} catch (final UnsupportedSchemeException ex) {
				throw new HttpException(ex.getMessage());
			}
		} else {
			target = host;
		}
		final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
		if (proxy == null) {
			return new HttpRoute(target, local, secure);
		} else {
			return new HttpRoute(target, local, proxy, secure);
		}
	}

	protected HttpHost determineProxy(final HttpHost target, final HttpRequest request, final HttpContext context) throws HttpException {
		return null;
	}

}
