package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class RomaniaCompari extends OrangeSelector {

	public RomaniaCompari() throws ConnectionException {
		super();
		setCountry("Romania");
		setSource("http://www.compari.ro/");
		Collection proxies = ProxyFinder.getProxies("Romania");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
