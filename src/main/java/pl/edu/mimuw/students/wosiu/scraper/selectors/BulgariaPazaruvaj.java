package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class BulgariaPazaruvaj extends OrangeSelector {

	public BulgariaPazaruvaj() throws ConnectionException {
		super();
		setCountry("Bulgaria");
		setSource("http://www.pazaruvaj.com/");
		Collection proxies = ProxyFinder.getInstance().getProxies("Bulgaria");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
