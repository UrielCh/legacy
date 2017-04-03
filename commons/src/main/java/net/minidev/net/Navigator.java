package net.minidev.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

/**
 * HTML navigator helper
 * 
 * @author Uriel Chemouni
 */
public class Navigator {
	public final static NoRedirectStrategy NO_REDIRECT = new NoRedirectStrategy();
	public final static NoCookieStore NO_COOKIE = new NoCookieStore();
	public boolean DROP_RESPONCE = false;
	public final static String USERAGENT = "Mozilla/5.0 Gecko/20090824 Firefox/3.5.3";
	private HttpClientBuilder client;
	private CloseableHttpClient clientI;
	private SSLContext sslcontext = null;
	private int statusCode;
	private CloseableHttpResponse lastResp;
	private URI lastUri;
	private boolean getBody = false;
	private String lastBody = null;
	private String forcedEncoding;
	private long maxSize = 1024 * 1024;

	private String proxy_user;
	private String proxy_password;
	private Proxy.Type proxy_type;

	public int cnxRetry = 1;
	public String urlEncoding = "UTF-8";

	private ArrayList<BasicHeader> additionnalHeader;

	static final ThreadLocal<byte[]> buffers = new ThreadLocal<byte[]>() {
		@Override
		protected byte[] initialValue() {
			return new byte[32000];
		}
	};

	public CloseableHttpClient getClientInstance() {
		if (clientI == null)
			clientI = client.build();
		return clientI;
	}

	public void close() {
	}

	public ArrayList<BasicHeader> getAdditionnalHeader() {
		return additionnalHeader;
	}

	public CloseableHttpResponse getLastResp() {
		return lastResp;
	}

	public URI getLastUri() {
		return lastUri;
	}

	public URI resolveUri(String path) {
		if (lastUri == null)
			try {
				return new URI(path);
			} catch (Exception e) {
				return null;
			}
		return lastUri.resolve(path);
	}

	public String getLastRedirect() {
		if (lastResp == null)
			return null;
		Header loc = lastResp.getFirstHeader("Location");
		if (loc != null) {
			return resolveUri(loc.getValue()).toString();
		}
		loc = lastResp.getFirstHeader("Refresh");
		if (loc != null) {
			String v = loc.getValue();
			if (v.startsWith("0; URL=")) {
				return v.substring(7);
			}
		}
		return null;
	}

	private ArrayList<String> warnings = new ArrayList<String>();

	public ArrayList<String> getWarnings() {
		return warnings;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public HttpClientBuilder getClient() {
		return client;
	}

	public void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
	}

	private String referer;

	public void setReferer(String url) {
		this.referer = url;
	}

	public void switchUserAgentIPhone() {
		String ua = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543 Safari/419.3";
		setUserAgent(ua);
	}

	public void switchUserAgentIE6() {
		String ua = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; GTB6)";
		setUserAgent(ua);
	}

	public void switchUserAgentIE7() {
		String ua = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; GTB6)";
		setUserAgent(ua);
	}

	public void switchUserAgentIE8() {
		String ua = "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/5.0; GTB6)";
		setUserAgent(ua);
	}

	public void switchUserAgentIE9() {
		String ua = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; GTB6)";
		setUserAgent(ua);
	}

	public void switchUserAgentIE(int version) {
		if (version == 6)
			switchUserAgentIE6();
		if (version == 7)
			switchUserAgentIE7();
		if (version == 8)
			switchUserAgentIE8();
		if (version == 9)
			switchUserAgentIE9();
	}

	public void setProxySocks(String host, int port) {
		X509HostnameVerifier hostnameVerifier;
		if (_ignoreSSL)
			hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		else
			hostnameVerifier = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		initSSL();

		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
		SSLConnectionSocketFactorySocks ssf = new SSLConnectionSocketFactorySocks(sslcontext, hostnameVerifier, proxy);
		getConnectionSocketFactory().register("http", new SocksSchemeSocketFactory(proxy));
		getConnectionSocketFactory().register("https", ssf);
		apply();
	}

	RegistryBuilder<ConnectionSocketFactory> connectionSocketFactory;

	public RegistryBuilder<ConnectionSocketFactory> getConnectionSocketFactory() {
		if (connectionSocketFactory == null) {
			connectionSocketFactory = RegistryBuilder.<ConnectionSocketFactory>create();
		}
		return connectionSocketFactory;
	}

	public void setProxy(String host, int port) {
		X509HostnameVerifier verif;
		if (_ignoreSSL)
			verif = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
		else
			verif = SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;
		initSSL();
		LayeredConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslcontext, verif);
		getConnectionSocketFactory().register("http", PlainConnectionSocketFactory.getSocketFactory());
		getConnectionSocketFactory().register("https", ssf);
		apply();
		client.setProxy(new HttpHost(host, port));
		getRequestConfig().setProxy(new HttpHost(host, port));
		apply();
		HttpHost proxy = new HttpHost(host, port, "http");
		getRequestConfig().setProxy(proxy);
		apply();
	}

	public void initSSL() {
		if (_ignoreSSL) {
			try {
				sslcontext = SSLContext.getInstance("SSL");
			} catch (Exception e) {
			}
			X509TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			try {
				sslcontext.init(null, new TrustManager[] { tm }, null);
			} catch (Exception e) {
			}
			client.setSslcontext(sslcontext);
		} else {
			sslcontext = SSLContexts.createDefault();
			client.setSslcontext(sslcontext);
		}
	}

	/**
	 * Initialized in case of change
	 */
	SocketConfig.Builder socketConfig;

	public SocketConfig.Builder getSocketConfig() {
		if (socketConfig == null)
			socketConfig = SocketConfig.custom();
		return socketConfig;
	}

	RequestConfig.Builder requestConfig;

	public RequestConfig.Builder getRequestConfig() {
		if (requestConfig == null)
			requestConfig = RequestConfig.custom();
		return requestConfig;
	}

	public void apply() {
		if (requestConfig != null)
			client.setDefaultRequestConfig(requestConfig.build());
		if (socketConfig != null)
			client.setDefaultSocketConfig(socketConfig.build());
		if (connectionSocketFactory != null) {
			Registry<ConnectionSocketFactory> socketFactoryRegistry = connectionSocketFactory.build();
			PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
			client.setConnectionManager(poolingmgr);
			if (socketConfig != null)
				client.setDefaultSocketConfig(socketConfig.build());
		}
	}

	/**
	 * set SO_TIMEOUT value
	 * 
	 * @param ms
	 *            time in Millisec
	 */
	public void setTimeOut(int ms) {
		getSocketConfig().setSoTimeout(ms);
		apply();
	}

	/**
	 * set CONNECTION_TIMEOUT value
	 * 
	 * @param ms
	 *            time in Millisec
	 */
	public void setCnxTimeOut(int ms) {
		getRequestConfig().setConnectTimeout(ms).setConnectionRequestTimeout(ms);
		apply();
	}

	/**
	 * Set the timeout in milliseconds until a connection is established.
	 */
	public void setConnectionTimeout(int ms) {
		setCnxTimeOut(ms);
	}

	/**
	 * Set the default socket timeout (SO_TIMEOUT) in milliseconds which is the timeout for waiting for data
	 */
	public void setSoTimeout(int ms) {
		setTimeOut(ms);
	}

	public void setLocalAdrress(String adress) throws UnknownHostException {
		getRequestConfig().setLocalAddress(InetAddress.getByName(adress));
		apply();
	}

	private String proxy = null;

	public String getProxy() {
		return proxy;
	}

	/**
	 * select a proxy to use, using the format proto://host supported format :
	 * 
	 * direct://[::]
	 * 
	 * direct://1.2.3.4
	 * 
	 * @param proxy
	 *            use the format: (proxy/socks)://host:port
	 * @throws UnknownHostException
	 */
	public void setProxy(String proxy) throws UnknownHostException {
		if (proxy == null)
			proxy = "direct://0.0.0.0";
		this.proxy = proxy;
		Pattern proxyPat = Pattern.compile("(\\w+)://(?:([^:]+)(?::([^@]+))?@)?([^:/]+)(?::(\\d+))?(?:\\s|$)");
		Pattern proxyPatV6 = Pattern.compile("(\\w+)://(?:([^:]+)(?::([^@]+))?@)?\\[([0-9a-fA-F:]+)\\](?::(\\d+))?(?:\\s|$)");
		Matcher m = proxyPat.matcher(proxy);
		if (!m.find()) {
			m = proxyPatV6.matcher(proxy);
			if (!m.find())
				throw new RuntimeException("can not parse proxy String: " + proxy);
		}
		String proto = m.group(1);
		proxy_user = m.group(2);
		proxy_password = m.group(3);
		String host = m.group(4);
		String sPort = null;
		sPort = m.group(5);
		int port = (sPort == null) ? 0 : Integer.parseInt(sPort);

		if (proto.equalsIgnoreCase("socks")) {
			if (port == 0)
				port = 1080;
			setProxySocks(host, port);
		} else if (proto.equalsIgnoreCase("proxy")) {
			if (port == 0)
				port = 3128;
			setProxy(host, port);
		} else if (proto.equalsIgnoreCase("direct")) {
			proxy_type = Type.DIRECT;
			setLocalAdrress(host);
		} else {
			throw new RuntimeException("Unsupported protocole: " + proto + " in " + proxy);
		}
	}

	/**
	 * Overwride UseAgent
	 */
	public void setUserAgent(String userAgent) {
		client.setUserAgent(userAgent);
	}

	public Navigator() {
		client = HttpClientBuilder.create();
		client.setRedirectStrategy(NO_REDIRECT);

		if (_ignoreSSL) {
			client.setHostnameVerifier(new X509HostnameVerifierIgnore());
			// client = WebClientDevWrapper.wrapClient(client);
		}
		resetCookie();
		setUserAgent(USERAGENT);
	}

	private static boolean _ignoreSSL = true;

	public static void ignoreSSL(boolean value) {
		_ignoreSSL = value;
	}

	public void allowRedirect(boolean allow) {
		if (allow)
			client.setRedirectStrategy(new DefaultRedirectStrategy());
		else
			client.setRedirectStrategy(new NoRedirectStrategy());
	}

	public void disableCookie() {
		client.setDefaultCookieStore(NO_COOKIE);
	}

	BasicCookieStore cookieStore;

	public void resetCookie() {
		cookieStore = new BasicCookieStore();
		this.setCookieStore(cookieStore);
	}

	public void setCookieStore(final CookieStore cookieStore) {
		client.setDefaultCookieStore(cookieStore);
		// client.setCookieStore(cookieStore);
	}

	public void addCookie(String name, String value, String domaine) {
		BasicClientCookie c = new BasicClientCookie(name, value);
		c.setDomain(domaine);
		getCookieStore().addCookie(c);
	}

	public void resetHeaders() {
		additionnalHeader = new ArrayList<BasicHeader>();
		additionnalHeader.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
		// ,sdch
	}

	public void addHeader(String name, String value) {
		if (additionnalHeader == null)
			resetHeaders();
		additionnalHeader.add(new BasicHeader(name, value));
	}

	public void removeHeader(String name) {
		if (additionnalHeader == null)
			resetHeaders();
		for (int i = additionnalHeader.size() - 1; i >= 0; i--) {
			BasicHeader header = additionnalHeader.get(i);
			if (name.equals(header.getName())) {
				additionnalHeader.remove(i);
			}
		}
	}

	public void setHeader(String name, String value) {
		if (additionnalHeader == null)
			resetHeaders();
		for (int i = 0; i < additionnalHeader.size(); i++) {
			BasicHeader header = additionnalHeader.get(i);
			if (name.equals(header.getName())) {
				additionnalHeader.set(i, new BasicHeader(name, value));
				return;
			}
		}
		addHeader(name, value);
	}

	public BasicCookieStore getCookieStore() {
		if (cookieStore == null)
			resetCookie();
		return cookieStore;
	}

	public Cookie getCookieByName(String name) {
		for (Cookie cookie : cookieStore.getCookies()) {
			if (!cookie.getName().equals(name))
				continue;
			return cookie;
		}
		return null;
	}

	public int getStatusCode(String url) throws IOException {
		HttpGet mtd = null;
		CloseableHttpResponse resp = null;
		try {
			mtd = new HttpGet(url);
			resp = getClientInstance().execute(mtd);
			int status = resp.getStatusLine().getStatusCode();
			statusCode = status;
			return status;
		} finally {
			if (mtd != null)
				mtd.abort();
			if (resp != null)
				resp.close();
		}
	}

	/**
	 * post mtd
	 * 
	 * ex: doPost("http://posturl", "param=1", param=2);
	 */
	public String doPost(String action, String... params) throws IOException {
		HttpPost post = new HttpPost(action);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String param : params) {
			int p = param.indexOf("=");
			if (p == -1)
				nvps.add(new BasicNameValuePair(param, ""));
			else
				nvps.add(new BasicNameValuePair(param.substring(0, p), param.substring(p + 1)));
		}
		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nvps, urlEncoding);
		post.setEntity(ent);
		return getBody(post);
	}

	/**
	 * @see StringEntity
	 * @see ByteArrayEntity
	 */
	public String doPost(String action, HttpEntity entry) throws IOException {
		HttpPost post = new HttpPost(action);
		post.setEntity(entry);
		return getBody(post);
	}

	public byte[] doPost4Data(String action, String... params) throws IOException {
		HttpPost post = new HttpPost(action);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String param : params) {
			int p = param.indexOf("=");
			if (p == -1)
				nvps.add(new BasicNameValuePair(param, ""));
			else
				nvps.add(new BasicNameValuePair(param.substring(0, p), param.substring(p + 1)));
		}
		UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nvps, urlEncoding);
		post.setEntity(ent);
		return getData(post);
	}

	/**
	 * post mtd
	 * 
	 * ex: doPost("http://posturl", "param=1", param=2);
	 */
	public String doGet(String action, String... params) throws IOException {
		if (params.length > 0) {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String param : params) {
				int p = param.indexOf("=");
				if (p == -1)
					nvps.add(new BasicNameValuePair(param, ""));
				else
					nvps.add(new BasicNameValuePair(param.substring(0, p), param.substring(p + 1)));
			}
			UrlEncodedFormEntity ent = new UrlEncodedFormEntity(nvps, urlEncoding);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			baos.write(action.getBytes());
			baos.write('?');
			ent.writeTo(baos);
			action = new String(baos.toByteArray());
		}
		return getBody(action);
	}

	public String getBody(String url) throws IOException {
		return getBody(new HttpGet(url));
	}

	public String getBody(URI url) throws IOException {
		return getBody(new HttpGet(url));
	}

	public String getBody(HttpRequestBase mtd) throws IOException {
		// mtd.setHeader("Accept-Encoding", "*");
		byte[] body = getData(mtd);
		Header ct = lastResp.getFirstHeader("Content-Type");
		if (ct != null) {
			String val = ct.getValue();
			int p = val.indexOf("charset=");
			if (p > 0) {
				String charSet = val.substring(p + 8);
				if (charSet.endsWith(";"))
					charSet = charSet.substring(0, charSet.length() - 1);
				if (charSet.equals("iso-88591"))
					charSet = "ISO-8859-1";
				// else if (charSet.equals("utf-8"))
				// charSet = "UTF-8";
				return new String(body, charSet);
			}
		}
		return new String(body);
	}

	public byte[] getData(String url) throws IOException {
		return getData(new HttpGet(url));
	}

	public byte[] getData(URI url) throws IOException {
		return getData(new HttpGet(url));
	}

	public synchronized byte[] getData(HttpRequestBase mtd) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		getData(mtd, baos);
		return baos.toByteArray();
	}

	public synchronized long getData(HttpRequestBase mtd, OutputStream os) throws IOException {
		try {
			if (proxy_user != null && proxy_password != null && proxy_type == Proxy.Type.HTTP) {
				String encoded = new String(Base64.encodeBase64((proxy_user + ":" + proxy_password).getBytes()));
				mtd.addHeader("Proxy-Authorization", "Basic " + encoded);
			}
			if (additionnalHeader != null)
				for (Header h : additionnalHeader)
					mtd.setHeader(h);

			if (referer != null) {
				mtd.addHeader(new BasicHeader("Referer", referer));
				referer = null;
			}
			IOException ioe = null;
			CloseableHttpResponse resp = null;
			for (int i = 0; i < cnxRetry; i++) {
				try {
					CloseableHttpClient closeableHttpClient = getClientInstance();
					resp = closeableHttpClient.execute(mtd, (HttpContext) null);
					break;
				} catch (IOException e) {
					ioe = e;
					e.fillInStackTrace();
				}
			}
			if (resp == null)
				throw new IOException("getData " + mtd.toString() + " fail after " + cnxRetry + " try", ioe);
			lastUri = mtd.getURI();
			lastResp = resp;
			statusCode = resp.getStatusLine().getStatusCode();

			//byte[] rr = 
			long size = getBoggusPage(resp, os);

			resp.close();
			mtd.abort();
			mtd.releaseConnection();
			close();
			return size; //rr;
		} finally {
			if (mtd != null)
				mtd.abort();
		}
	}

	/**
	 * @param urlSrc
	 * @param limit
	 *            max authorized request count.
	 * @return
	 * @throws IOException
	 */
	public String getLocation(String urlSrc, int limit) throws IOException {
		String url = urlSrc;
		if (limit <= 0)
			throw new NullPointerException("limit mist be > 0");

		while (limit-- >= 0) {
			HttpGet mtd = new HttpGet(url);
			CloseableHttpResponse response = null;
			try {
				URI uri = new URI(url);
				response = getClientInstance().execute(mtd);
				Header loc = response.getFirstHeader("Location");
				if (loc == null) {
					return url;
				}
				setReferer(url);
				url = uri.resolve(loc.getValue()).toString();
			} catch (Exception e) {
				warnings.add("Error importing : " + urlSrc + " - " + e.getMessage());
				break;
			} finally {
				if (response != null)
					response.close();
				if (getBody) {
					byte[] b = getBoggusPage(response);
					if (forcedEncoding != null)
						lastBody = new String(b, forcedEncoding);
					else
						lastBody = new String(b);
				}
				mtd.abort();
			}
		}
		return url; // error ..
	}

	public String getLastBody() {
		return lastBody;
	}

	/**
	 * Remap Server IP code
	 */
	public RoutePlannerRedirect routePlanner = null;

	public void redirectHost(String from, InetAddress to) {
		if (routePlanner == null) {
			routePlanner = new RoutePlannerRedirect(null);
			this.client.setRoutePlanner(routePlanner);
		}
		routePlanner.map(from, to);

	}

	public byte[] getBoggusPage(CloseableHttpResponse response) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		getBoggusPage(response, baos);
		return baos.toByteArray();
	}

	public long getBoggusPage(CloseableHttpResponse response, OutputStream os) throws IOException {
		HttpEntity entity = response.getEntity();
		if (entity == null)
			return 0;
		Header h = response.getFirstHeader("Content-Encoding");
		if (h != null && h.getValue().equals("gzip"))
			entity = new GzipDecompressingEntity(entity);

		InputStream is = entity.getContent();
		// InputStream is = entity.getContent();
		// if (response.getFirstHeader("Content-Encoding").equals("gzip")) {
		// }
		long size = 0;
		byte buf[] = buffers.get();

		int c;
		try {
			while ((c = is.read(buf)) > 0) {
				if (DROP_RESPONCE)
					size += c;
				else
					os.write(buf, 0, c);
				if (size >= maxSize) {
					break;
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}

		// finir avec un buffer de 1
		if (size < maxSize) {
			buf = new byte[1];
			try {
				while ((c = is.read(buf)) > 0) {
					if (DROP_RESPONCE)
						size += c;
					else
						os.write(buf, 0, c);
					if (size > maxSize) {
						break;
					}
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		try {
			is.close();
		} catch (Exception e) {
		}

		if (DROP_RESPONCE) {
			StringBuffer sb = new StringBuffer();
			sb.append("DROP responce: ");
			String sizeS = Long.toString(size);
			while (sizeS.length() > 3) {
				int i = sizeS.length() % 3;
				if (i == 0)
					i = 3;
				sb.append(sizeS.substring(0, i));
				sizeS = sizeS.substring(i);
				sb.append(" ");
			}
			sb.append(sizeS);
			sb.append("o");
			return size;// (sb.toString()).getBytes();
		}
		return size;// baos.toByteArray();
	}

	public static class NoRedirectStrategy implements RedirectStrategy {
		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
			return null;
		}

		@Override
		public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
			return false;
		}
	}

	public static class NoCookieStore implements CookieStore {
		@Override
		public void addCookie(Cookie cookie) {
		}

		@Override
		public void clear() {
		}

		@Override
		public boolean clearExpired(Date date) {
			return false;
		}

		@Override
		public List<Cookie> getCookies() {
			return null;
		}
	}

	/*
	 * 
	 * 
	 * static class MySchemeSocketFactory implements SchemeSocketFactory { public Socket createSocket() throws
	 * IOException { return new Socket(); }
	 * 
	 * public Socket connectSocket( final Socket socket, final InetSocketAddress remoteAddress, final InetSocketAddress
	 * localAddress, final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException { if
	 * (remoteAddress == null) { throw new IllegalArgumentException("Remote address may not be null"); } if (params ==
	 * null) { throw new IllegalArgumentException("HTTP parameters may not be null"); } String proxyHost = (String)
	 * params.getParameter("socks.host"); Integer proxyPort = (Integer) params.getParameter("socks.port");
	 * 
	 * InetSocketAddress socksaddr = new InetSocketAddress(proxyHost, proxyPort); Proxy proxy = new
	 * Proxy(Proxy.Type.SOCKS, socksaddr); Socket sock = new Socket(proxy); if (localAddress != null) {
	 * sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params)); sock.bind(localAddress); } int timeout =
	 * HttpConnectionParams.getConnectionTimeout(params); try { sock.connect(remoteAddress, timeout); } catch
	 * (SocketTimeoutException ex) { throw new ConnectTimeoutException("Connect to " + remoteAddress.getHostName() "/" +
	 * remoteAddress.getAddress() + " timed out"); } return sock; }
	 * 
	 * public boolean isSecure(final Socket sock) throws IllegalArgumentException { return false; }
	 * 
	 * }
	 */
}
