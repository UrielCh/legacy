package net.minidev.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLInitializationException;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/**
 * 
 * @see SSLConnectionSocketFactory
 * 
 */
public class SSLConnectionSocketFactorySocks implements LayeredConnectionSocketFactory {
	/**
	 * Obtains default SSL socket factory with an SSL context based on the
	 * standard JSSE trust material (<code>cacerts</code> file in the security
	 * properties directory). System properties are not taken into
	 * consideration.
	 * 
	 * @return default SSL socket factory
	 */
	public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
		return new SSLConnectionSocketFactory(SSLContexts.createDefault(), SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	}

	private final SSLSocketFactory sslSocketfactory;
	private final X509HostnameVerifier hostnameVerifier;
	Proxy proxy;

	public SSLConnectionSocketFactorySocks(final SSLContext sslContext, String host, int port) {
		this(sslContext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER, host, port);
	}

	public SSLConnectionSocketFactorySocks(final SSLContext sslContext, Proxy proxy) {
		this(sslContext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER, proxy);
	}

	public SSLConnectionSocketFactorySocks(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier, String host, int port) {
		this(Args.notNull(sslContext, "SSL context").getSocketFactory(), hostnameVerifier, host, port);
	}

	public SSLConnectionSocketFactorySocks(final SSLContext sslContext, final X509HostnameVerifier hostnameVerifier, Proxy proxy) {
		this(Args.notNull(sslContext, "SSL context").getSocketFactory(), hostnameVerifier, proxy);
	}

	public SSLConnectionSocketFactorySocks(final SSLSocketFactory socketfactory, final X509HostnameVerifier hostnameVerifier, String host,
			int port) {
		this(socketfactory, hostnameVerifier, new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port)));
	}

	public SSLConnectionSocketFactorySocks(final SSLSocketFactory socketfactory, final X509HostnameVerifier hostnameVerifier, Proxy proxy) {
		this.sslSocketfactory = Args.notNull(socketfactory, "SSL socket factory");
		this.hostnameVerifier = hostnameVerifier != null ? hostnameVerifier : SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		this.proxy = proxy;
	}

	/**
	 * Performs any custom initialization for a newly created SSLSocket (before
	 * the SSL handshake happens).
	 * 
	 * The default implementation is a no-op, but could be overriden to, e.g.,
	 * call {@link javax.net.ssl.SSLSocket#setEnabledCipherSuites(String[])}.
	 */
	protected void prepareSocket(final SSLSocket socket) throws IOException {
	}

	private void internalPrepareSocket(final SSLSocket socket) throws IOException {
		prepareSocket(socket);
	}

	public Socket createSocket(final HttpContext context) throws IOException {
		// https://code.google.com/p/conf-bot/source/browse/trunk/ConfBot/src/confbot/SSLTunnelSocketFactory.java?r=3
		// Socket tunnel = new Socket(proxy);
		final SSLSocket sock = (SSLSocket) sslSocketfactory.createSocket();
		internalPrepareSocket(sock);
		return sock;
	}

	public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress,
			final InetSocketAddress localAddress, final HttpContext context) throws IOException {
		Args.notNull(host, "HTTP host");
		Args.notNull(remoteAddress, "Remote address");
		final Socket sock = socket != null ? socket : createSocket(context);
		if (localAddress != null) {
			sock.bind(localAddress);
		}
		try {
			sock.connect(remoteAddress, connectTimeout);
		} catch (final IOException ex) {
			try {
				sock.close();
			} catch (final IOException ignore) {
			}
			throw ex;
		}
		// Setup SSL layering if necessary
		if (sock instanceof SSLSocket) {
			final SSLSocket sslsock = (SSLSocket) sock;
			sslsock.startHandshake();
			verifyHostname(sslsock, host.getHostName());
			return sock;
		} else {
			return createLayeredSocket(sock, host.getHostName(), remoteAddress.getPort(), context);
		}
	}

	public Socket createLayeredSocket(final Socket socket, final String target, final int port, final HttpContext context) throws IOException {
		final SSLSocket sslsock = (SSLSocket) sslSocketfactory.createSocket(socket, target, port, true);
		internalPrepareSocket(sslsock);
		sslsock.startHandshake();
		verifyHostname(sslsock, target);
		return sslsock;
	}

	X509HostnameVerifier getHostnameVerifier() {
		return this.hostnameVerifier;
	}

	private void verifyHostname(final SSLSocket sslsock, final String hostname) throws IOException {
		try {
			this.hostnameVerifier.verify(hostname, sslsock);
			// verifyHostName() didn't blowup - good!
		} catch (final IOException iox) {
			// close the socket before re-throwing the exception
			try {
				sslsock.close();
			} catch (final Exception x) { /* ignore */
			}
			throw iox;
		}
	}
}
