package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ProxyFinder {
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 " +
			"(KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
	private static Map<String, Set<Proxy>> proxyMap;
	private static ProxyFinder instance = null;
	private static Selector[] proxySelecctors = {};
	private static Logger logger = Logger.getLogger(ProxyFinder.class);

	protected ProxyFinder() {
		proxyMap = new HashMap<>();
		for (Selector selector : proxySelecctors) {
			URL source = selector.getSourceURL();
			List<Object> res;
			try {
				res = selector.traverseAndCollectProducts(USER_AGENT, source);
			} catch (ConnectionException e) {
				logger.error("Cannot download proxy list from: " + source);
				continue;
			}
			if (res != null && !res.isEmpty()) {
				for (Object o : res) {
					ProxyWrapper proxyW = (ProxyWrapper) o;
					String country = proxyW.getCountry();
					Set<Proxy> set = proxyMap.get(country);
					if (set == null) {
						set = new HashSet<>();
						proxyMap.put(country, set);
					}
					set.add(proxyW.getProxy());
				}
				logger.debug("Added " + res.size() + " proxies.");
			}
		}
	}

	public static ProxyFinder getInstance() {
		if (instance == null) {
			instance = new ProxyFinder();
		}
		return instance;
	}

	public static Set<Proxy> getProxies(String country) {
		Set<Proxy> proxies = getInstance().proxyMap.get(country);
		return proxies;
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		Set<Proxy> german = ProxyFinder.getProxies("Germany");
		System.out.println(german);
	}
}