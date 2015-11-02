package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedList;
import java.util.List;

public abstract class Selector {
	private String country = null;
	private URL sourceURL = null;
	private List<Proxy>proxies = null;

	protected Logger logger;

	public Selector() {
		logger = Logger.getLogger(this.getClass());
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setSource(String sourceURL) throws URISyntaxException, MalformedURLException {
		URI uri = new URI(sourceURL);
		this.sourceURL = uri.toURL();
	}

	public void addProxy(String ip, int port) {
		if (proxies == null) {
			proxies = new LinkedList<>();
		}
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		proxies.add(proxy);
	}

	public abstract URL prepareTargetUrl(String product);

	public abstract List<Object> getProducts(Document document);

	public abstract URL getNextPage(Document document);


	public HttpURLConnection connect(String userAgent, URL targetURL) throws ConnectionException {
		HttpURLConnection uc = null;
		for (Proxy proxy: proxies) {
			// Try to connect via proxy. If failed try next one.
			try {
				uc = (HttpURLConnection) targetURL.openConnection(proxy);
				uc.setRequestProperty("User-Agent", userAgent);
				uc.connect();
				return uc;
			} catch (IOException e) {
				logger.warn("Cannot connect to: " + targetURL + ", using proxy server: " + proxy + ". Trying next " +
						"proxy server..");
				logger.debug(e.getMessage());
				continue;
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

	public URL stringToURL(String url) throws URISyntaxException, MalformedURLException {
		// create ConnectionException
		URI uri = new URI(url);
		URL targetURL = uri.toURL();
		return targetURL;
	}


	public Document getDoc(String userAgent, URL targetURL) throws ConnectionException {
		HttpURLConnection connection = connect(userAgent, targetURL);
		String content = null;
		try {
			content = download(connection);
		} catch (IOException e) {
			logger.debug(e.getMessage());
			throw new ConnectionException("Cannot read content from connection: " + targetURL + ", userAgent:  "
					+ userAgent);
		}
		Document doc = Jsoup.parse(content);
		disconnect(connection);
		return doc;
	}
}
