package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class DenmarkKelkoo extends KelkooSelector {

	public DenmarkKelkoo() throws ConnectionException {
		super();
		setCountry("Denmark");
		setSource("http://www.kelkoo.dk/");
		Collection proxies = ProxyFinder.getProxies("Denmark");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
