package net.minidev.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public class SocksSchemeSocketFactory implements LayeredConnectionSocketFactory /* ConnectionSocketFactory */{
	Proxy proxy;

	public SocksSchemeSocketFactory(Proxy proxy) {
		this.proxy = proxy;
	}

	public SocksSchemeSocketFactory(String proxyHost, int proxyPort) {
		this(new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort)));
	}

	@Override
	public Socket createSocket(org.apache.http.protocol.HttpContext context) throws IOException {
		Socket sock = new Socket(proxy);
		return sock;
	};

	@Override
	public Socket connectSocket(int connectTimeout, Socket sock, org.apache.http.HttpHost host, InetSocketAddress remoteAddress,
			InetSocketAddress localAddress, org.apache.http.protocol.HttpContext context) throws IOException {
		if (sock == null) {
			sock = createSocket(context);
		}

		if (localAddress != null) {
			// sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			sock.bind(localAddress);
		}
		
		// int timeout = HttpConnectionParams.getConnectionTimeout(params);

		try {
			sock.connect(remoteAddress, connectTimeout);
		} catch (SocketTimeoutException ex) {
			throw new ConnectTimeoutException("Connect to " + remoteAddress + " timed out");
		}
		return sock;
	}

	@Override
	public Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException, UnknownHostException {
		Socket sock = new Socket(proxy);
		return sock;
	}

	// @Override
	// public boolean isSecure(Socket sock) throws IllegalArgumentException {
	// if (sock == null) {
	// throw new IllegalArgumentException("Socket may not be null.");
	// }
	// // This check is performed last since it calls a method implemented
	// // by the argument object. getClass() is final in java.lang.Object.
	// if (sock.isClosed()) {
	// throw new IllegalArgumentException("Socket is closed.");
	// }
	// return false;
	// }

}
