package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public abstract class Selector {

	private static int CONNECTION_TIMEOUT_MS = 4000;
	private static int READ_TIMEOUT_MS = 7000;

	private String country = null;
	private URL sourceURL = null;
	private List<Proxy> proxies = null;
	private Proxy lastUsedProxy = null;

	protected Logger logger;

	public Selector() {
		logger = Logger.getLogger(this.getClass());
		proxies = new LinkedList<>();
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setSource(String sourceURL) throws ConnectionException {
		this.sourceURL = Utils.stringToURL(sourceURL);
	}

	public String getCountry() {
		return country;
	}

	public Proxy getLastUsedProxy() {
		return lastUsedProxy;
	}

	public URL getSourceURL() {
		return sourceURL;
	}

	public void addProxy(String ip, int port) {
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		proxies.add(proxy);
	}

	public void addAllProxies(Collection<Proxy> collection) {
		proxies.addAll(collection);
	}

	public void addProxy(Proxy proxy) {
		proxies.add(proxy);
	}

	public abstract URL prepareTargetUrl(String product) throws ConnectionException;

	public abstract List<Object> getProducts(Document document);

	public abstract URL getNextPage(Document document);

	public HttpURLConnection connectByProxy(String userAgent, URL targetURL, Proxy proxy) throws IOException {
		long start = System.currentTimeMillis();
		HttpURLConnection uc = (HttpURLConnection) targetURL.openConnection(proxy);
		uc.setRequestProperty("User-Agent", userAgent);
		uc.setConnectTimeout(CONNECTION_TIMEOUT_MS);
		uc.setReadTimeout(READ_TIMEOUT_MS);
		uc.connect();
		long elapsed = (System.currentTimeMillis() - start) / 1000;
		logger.debug("Connected in: " + elapsed + "ms using proxy: " + proxy);
		return uc;
	}

	public HttpURLConnection connect(String userAgent, URL targetURL) throws ConnectionException {
		HttpURLConnection uc = null;

		if (lastUsedProxy != null) {
			try {
				return connectByProxy(userAgent, targetURL, lastUsedProxy);
			} catch (IOException e) {
				logger.warn("Cannot use last used proxy: " + lastUsedProxy);
				logger.debug(e.toString());
			}
		}


		if (proxies != null) {
			for (Proxy proxy : proxies) {
				// Try to connect via proxy. If failed try next one.
				try {
					uc = connectByProxy(userAgent, targetURL, proxy);
					lastUsedProxy = proxy;
					return uc;
				} catch (IOException e) {
					logger.warn("Cannot connect to: " + targetURL + ", using proxy server: " + proxy + ". Trying next " +
							"proxy server..");
					logger.debug(e.toString());
					continue;
				}
			}
		}

		// if all proxies failed try to connect directly
		try {
			logger.info("Connecting directly (from local IP) to: " + targetURL);
			uc = (HttpURLConnection) targetURL.openConnection();
			uc.setRequestProperty("User-Agent", userAgent);
			uc.connect();
			return uc;
		} catch (IOException e) {
			logger.warn("Cannot connect directly to: " + targetURL);
		}

		throw new ConnectionException("Cannot connect to: " + targetURL + ", userAgent: " + userAgent);
	}

	public void disconnect(HttpURLConnection connection) {
		connection.disconnect();
	}

	public String download(HttpURLConnection connection) throws IOException {
		String line;
		StringBuffer contentBuilder = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(connection.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		while ((line = in.readLine()) != null) {
			contentBuilder.append(line + "\n");
		}

		return contentBuilder.toString();
	}

	public Document getDoc(String userAgent, URL targetURL) throws ConnectionException {
		HttpURLConnection connection = connect(userAgent, targetURL);
		String content = null;
		try {
			content = download(connection);
		} catch (IOException e) {
			logger.debug(e.toString());
			disconnect(connection);
			throw new ConnectionException("Cannot read content from connection: " + targetURL + ", userAgent:  "
					+ userAgent);
		}
		Document doc = Jsoup.parse(content);
		disconnect(connection);
		return doc;
	}


	/**
	 * Go thorough pagination list using `getNextPage`, get content of site using `getDoc`
	 * and collect products using `getProducts`.
	 *
	 * @param userAgent
	 * @param startUrl
	 * @return
	 */
	public List<Object> traverseAndCollectProducts(String userAgent, URL startUrl) throws ConnectionException {
		List<Object> results = new LinkedList<>();
		for (URL targetURL = startUrl; targetURL != null; ) {
			Document doc = getDoc(userAgent, targetURL);
			List prods = getProducts(doc);
			if (prods != null) {
				results.addAll(prods);
			}
			targetURL = getNextPage(doc);
		}
		return results;
	}

}
