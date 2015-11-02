package pl.edu.mimuw.students.wosiu.scraper;

import java.net.*;
import java.util.LinkedList;
import java.util.List;

public class Selector {
	private String country = null;
	private URL sourceURL = null;
	private List<Proxy>proxies = null;

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

}
