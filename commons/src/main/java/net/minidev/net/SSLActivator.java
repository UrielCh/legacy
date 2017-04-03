package net.minidev.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class SSLActivator {
	private String passphrase;
	private int newKeyCount;
	private final static byte[] EMPTY_KS = new byte[] { -2, -19, -2, -19, 0, 0, 0, 2, 0, 0, 0, 0, 125, 125, 108, -113,
			33, -75, 71, -93, -49, 42, -38, -40, 50, 93, 123, 67, -36, -100, -67, 101 };
	public final static String DEFAULT_PASS = "changeit";

	public static void authorize(String... hosts) throws GeneralSecurityException, IOException {
		File caFile = getJavaDefaultKeyStoreFile();
		SSLActivator act = new SSLActivator(caFile, DEFAULT_PASS);
		for (String host : hosts) {
			System.out.println("Try to authorize : https://" + host + "/");
			act.authorized(host);
		}
	}

	public int getNewKeyCount() {
		return newKeyCount;
	}

	public SSLActivator(File trustStore) throws IOException {
		this(trustStore, "myStore");
	}

	public SSLActivator() throws IOException {
		this(getJavaDefaultKeyStoreFile(), DEFAULT_PASS);
	}

	public SSLActivator(File trustStore, String password) throws IOException {
		if (!trustStore.exists()) {
			FileOutputStream fos = new FileOutputStream(trustStore);
			fos.write(EMPTY_KS);
			fos.close();
		}
		System.getProperties().put("javax.net.ssl.trustStore", trustStore.getAbsolutePath());
		System.getProperties().put("javax.net.ssl.trustStorePassword", password);
		this.newKeyCount = 0;
		this.passphrase = password;
	}

	private static File getJavaDefaultKeyStoreFile() {
		File f = new File(System.getProperty("java.home"));
		f = new File(f, "lib");
		f = new File(f, "security");
		File f1 = new File(f, "jssecacerts");
		File f2 = new File(f, "cacerts");
		if (f1.exists())
			return f1;
		else if (f2.exists())
			return f2;
		return f2;
	}

	private File getKeyStoreFile() {
		String ks = System.getProperty("javax.net.ssl.trustStore");
		if (ks == null) {
			File f = new File(System.getProperty("java.home"));
			f = new File(f, "lib");
			f = new File(f, "security");
			File f1 = new File(f, "jssecacerts");
			File f2 = new File(f, "cacerts");
			if (f1.exists())
				ks = f1.getAbsolutePath();
			else if (f2.exists())
				ks = f1.getAbsolutePath();
			if (ks == null)
				ks = f1.getAbsolutePath();
		}
		return new File(ks);
	}

	public void authorized(String host) throws GeneralSecurityException, IOException {
		authorized(host, 443);
	}

	public void authorized(String host, int port) throws GeneralSecurityException, IOException {
		X509Certificate certs[] = this.getMissingCerts(host, port);
		if (certs.length == 0)
			System.out.println("No missing Certificat found");
		else {
			System.out.println("Adding " + certs.length + " certificat");
		}
		this.authorized(certs, host);
	}

	private KeyStore getKeyStore() throws GeneralSecurityException, IOException {
		File file = getKeyStoreFile();
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		if (file.exists()) {
			InputStream in = new FileInputStream(file);
			keyStore.load(in, passphrase.toCharArray());
			in.close();
		}
		return keyStore;
	}

	private void saveKeyStore(KeyStore keyStore) throws GeneralSecurityException, IOException {
		File file = getKeyStoreFile();
		OutputStream out = new FileOutputStream(file);
		keyStore.store(out, passphrase.toCharArray());
		out.close();
	}

	public boolean haveCert(String host) throws GeneralSecurityException, IOException {
		KeyStore keyStore = getKeyStore();
		return (keyStore.getCertificate(host) != null);
	}
// X509Certificate
	public void authorized(Certificate[] certs, String host) throws GeneralSecurityException, IOException {
		KeyStore keyStore = getKeyStore();
		String alias = host;
		for (Certificate cert : certs) {
			keyStore.setCertificateEntry(alias, cert);
			newKeyCount++;
		}
		saveKeyStore(keyStore);
	}

	public X509Certificate[] getMissingCerts(String host, int port) throws GeneralSecurityException, IOException {
		KeyStore keyStore = getKeyStore();

		X509Certificate[] chain = null;
		SSLContext context = SSLContext.getInstance("TLS");
		String defaultAlgo = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(defaultAlgo);
		tmf.init(keyStore);

		X509TrustManager defaultTrustManager;
		defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
		CatchCert tm = new CatchCert(defaultTrustManager);
		context.init(null, new TrustManager[] { tm }, null);
		SSLSocketFactory factory = context.getSocketFactory();
		
		// Opening connection to host:port
		SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
		socket.setSoTimeout(10000);

		try {
			// Starting SSL handshake
			socket.startHandshake();
			socket.close();
			chain = new X509Certificate[0];
		} catch (SSLException e) {
			// Certificate not in Keystore, retrieve certificate chain
			chain = tm.chain;
		}
		return chain;
	}

	private static class CatchCert implements X509TrustManager {
		private final X509TrustManager tm;
		public X509Certificate[] chain;

		public CatchCert(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain1, String authType) throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain1, String authType) throws CertificateException {
			this.chain = chain1;
			tm.checkServerTrusted(chain1, authType);
		}
	}
}
