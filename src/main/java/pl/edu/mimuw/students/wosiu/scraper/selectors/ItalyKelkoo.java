package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class ItalyKelkoo extends KelkooSelector {

	public ItalyKelkoo() throws ConnectionException {
		super();
		setCountry("Italy");
		setSource("http://www.kelkoo.it/");
		Collection proxies = ProxyFinder.getInstance().getProxies("Italy");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
