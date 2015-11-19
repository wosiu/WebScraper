package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

public abstract class Selector {

	private static int CONNECTION_TIMEOUT_MS = 4000;
	private static int READ_TIMEOUT_MS = 10000;
	private static int LOCAL_CONNECTION_TIMEOUT_MS = 40 * 1000;
	private static int LOCAL_READ_TIMEOUT_MS = 40 * 1000;


	private String country = null;
	private URL sourceURL = null;
	private List<Proxy> proxies = null;
	private Proxy lastUsedProxy = null;
	private int MAX_RESULTS_NUM = -1; // -1 = inf

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

	public abstract Object getProducts(Document document);

	public abstract List<URL> getNextPages(Document document);


	/**
	 * @return true if network has been down, false if everything with network is ok.
	 */
	public boolean waitForNetwork() throws InterruptedException {
		boolean ret = false;
		boolean netAccess = false;
		for (int ms = 1000; ; ms = Math.min(ms + 1000, 20000)) {

			try {
				Socket socket = new Socket("www.google.com", 80);
				netAccess = socket.isConnected();
				socket.close();
			} catch (IOException e) {
				ret = true;
				netAccess = false;
			}

			if (netAccess) {
				return ret;
			}

			logger.info("Waiting for network " + ms + " ms");
			Thread.sleep(ms);
		}
	}

	/**
	 * Download `targetURL` regarding only connection wrapper given as argument
	 * @param targetURL
	 * @param cw
	 * @return
	 * @throws ConnectionException
	 */
	public Document download(URL targetURL, ConnectionWrapper cw) throws ConnectionException {
		final String connectionInfoMsg = "url: " + targetURL.toString() + ", " + cw.toString();
		HttpURLConnection uc = null;

		try {
			do {
				try {
					long start = System.currentTimeMillis();
					uc = cw.connect(targetURL);
					long elapsed = (System.currentTimeMillis() - start) / 1000;
					logger.debug("Connected in: " + elapsed + "s to: " + targetURL + ", " + cw);

					Document doc = read(uc);
					return doc;

				} catch (ConnectException | UnknownHostException e) {
					// common reasons:
					// ConnectException: Połączenie odrzucone, Sieć jest niedostępna
					// UnknownHostException: `host`
					logger.info(connectionInfoMsg);
					logger.info("Reconnecting...");
					continue;
				} catch (SocketTimeoutException | SocketException e) {
					// common reasons:
					// SocketTimeoutException: Read timed out / connect timed out
					// SocketException: Connection reset / Unexpected end of file from server
					logger.warn(connectionInfoMsg);
					logger.warn(e.toString());
					break;
				} catch (IOException e) {
					logger.error(connectionInfoMsg);
					logger.error(e.toString());
					break;
				} finally {
					if (uc != null) {
						uc.disconnect();
					}
				}
			} while (waitForNetwork());
			
		} catch (InterruptedException e) {
			logger.error("Cannot sleep thread while waiting for reconnect");
		}

		return null;
	}


	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		Document document = null;
		ConnectionWrapper cw = new ConnectionWrapper();
		cw.setUserAgent(userAgent);
		cw.setConnectionTimeoutMs(CONNECTION_TIMEOUT_MS);
		cw.setReadTimeoutMs(READ_TIMEOUT_MS);

		if (lastUsedProxy != null) {
			cw.setProxy(lastUsedProxy);
			document = download(targetURL, cw);
			if (document != null) {
				return document;
			}
			logger.warn("Cannot use last used proxy: " + lastUsedProxy);
		}

		if (proxies != null) {
			for (Proxy proxy : proxies) {
				cw.setProxy(proxy);
				document = download(targetURL, cw);
				if (document != null) {
					lastUsedProxy = proxy;
					return document;
				}
				logger.warn("Trying next proxy server..");
			}
		}

		lastUsedProxy = null;

		// if all proxies failed try to download directly
		logger.info("Connecting directly (from local IP) to: " + targetURL);
		cw.setProxy(null);
		cw.setConnectionTimeoutMs(LOCAL_CONNECTION_TIMEOUT_MS);
		cw.setReadTimeoutMs(LOCAL_READ_TIMEOUT_MS);
		document = download(targetURL, cw);
		if (document != null) {
			return document;
		}

		logger.error("Cannot connect directly to: " + targetURL);
		throw new ConnectionException("Cannot connect to: " + targetURL + ", userAgent: " + userAgent);
	}

	/**
	 * Read and disconnect connection
	 */
	public Document read(HttpURLConnection connection) throws IOException {
		long start = System.currentTimeMillis();
		String line;
		StringBuffer contentBuilder = new StringBuffer();
		InputStreamReader isr = new InputStreamReader(connection.getInputStream());
		BufferedReader in = new BufferedReader(isr);

		while ((line = in.readLine()) != null) {
			contentBuilder.append(line + "\n");
		}

		connection.disconnect();

		String content = contentBuilder.toString();
		Document doc = Jsoup.parse(content);

		if (getSourceURL() != null) {
			doc.setBaseUri(getSourceURL().toString());
		}

		long elapsed = (System.currentTimeMillis() - start) / 1000;
		logger.debug("Read in: " + elapsed + "s ");

		return doc;
	}

	/**
	 * Go thorough pagination list using `getNextPages`, get content of site using `getDoc`
	 * and collect products using `getProducts`.
	 *
	 * @param userAgent
	 * @param startUrl
	 * @return
	 */
	public List<Object> traverseAndCollectProducts(String userAgent, URL startUrl) {
		List<Object> results = new LinkedList<>();
		LinkedList<URL> urlToVisit = new LinkedList<>();
		Set<URL> urlVisited = new HashSet<>();
		urlToVisit.add(startUrl);

		while (!urlToVisit.isEmpty()) {
			URL targetURL = urlToVisit.removeFirst();
			if (!urlVisited.add(targetURL)) {
				continue;
			}
			logger.debug("Collecting from: " + targetURL);
			Document doc;
			try {
				doc = download(userAgent, targetURL);
			} catch (ConnectionException e) {
				logger.warn("Cannot read: " + targetURL);
				logger.debug(e);
				continue;
				// if cannot process some page, return results collected by now if any and forget about next pages
				/*if ( results.isEmpty() ) {
					throw e;
				} else {
					logger.info("Return partly result, as cannot read: " + targetURL);
					return results;
				}*/
			}
			long start = System.currentTimeMillis();
			List prods = (List) getProducts(doc);
			long elapsed = (System.currentTimeMillis() - start) / 1000;
			logger.debug("Got " + ((prods == null) ? 0 : prods.size()) + " products in: " + elapsed + "s ");

			if (prods != null) {
				if (MAX_RESULTS_NUM != -1 && results.size() + prods.size() > MAX_RESULTS_NUM) {
					results.addAll(prods.subList(0, MAX_RESULTS_NUM - results.size()));
					return results;
				}
				results.addAll(prods);
			}

			List<URL> nexts = getNextPages(doc);
			if (nexts != null) {
				urlToVisit.addAll(nexts);
			}
			logger.debug("Got " + ((nexts == null) ? 0 : nexts.size()) + " next URLS");
		}
		return results;
	}
}
