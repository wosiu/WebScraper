package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class SwedenPrisjakt extends PricespySelector {

	public SwedenPrisjakt() throws ConnectionException {
		super();
		setCountry("Sweden");
		setSource("http://www.prisjakt.nu/");
		Collection proxies = ProxyFinder.getProxies("Sweden");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
