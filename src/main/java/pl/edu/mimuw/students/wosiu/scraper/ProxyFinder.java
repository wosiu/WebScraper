package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Gatherproxy;

import java.io.IOException;
import java.net.Proxy;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

// TODO Think obout "online" version of ProxyFinder - do not search until ask for proxies from specific country
public class ProxyFinder {
	private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 " +
			"(KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
	private static Map<String, Set<Proxy>> proxyMap;
	private static ProxyFinder instance = null;
	private static Logger logger = Logger.getLogger(ProxyFinder.class);


	private static Selector[] proxySelecctors = {
			new Gatherproxy("Austria"),
			new Gatherproxy("Belgium"),
			new Gatherproxy("Bulgaria"),
			new Gatherproxy("Croatia"),
			new Gatherproxy("Czech%20Republic"),
			new Gatherproxy("Denmark"),
			new Gatherproxy("Estonia"),
			new Gatherproxy("Finland"),
			new Gatherproxy("France"),
			new Gatherproxy("Greece"),
			new Gatherproxy("Netherlands"),
			new Gatherproxy("Ireland"),
			new Gatherproxy("Lithuania"),
			new Gatherproxy("Latvia"),
			new Gatherproxy("Germany"),
			new Gatherproxy("Portugal"),
			new Gatherproxy("Romania"),
			new Gatherproxy("Slovakia"),
			new Gatherproxy("Slovenia"),
			new Gatherproxy("Sweden"),
			new Gatherproxy("United%20Kingdom"),
			new Gatherproxy("Hungary"),
			new Gatherproxy("Italy"),
			new Gatherproxy("Poland"),
	};


	protected ProxyFinder() {
		proxyMap = new HashMap<>();
		for (Selector selector : proxySelecctors) {
			URL source = selector.getSourceURL();
			List<Object> res;
			res = selector.traverseAndCollectProducts(USER_AGENT, source);

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
			} else {
				logger.error("Cannot download proxy list from: " + source);
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

	public static Map getProxies() {
		return getInstance().proxyMap;
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.out.println(ProxyFinder.getProxies());
	}
}
