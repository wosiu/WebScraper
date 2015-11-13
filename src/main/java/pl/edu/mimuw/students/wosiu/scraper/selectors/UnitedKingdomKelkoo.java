package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class UnitedKingdomKelkoo extends KelkooSelector {

	public UnitedKingdomKelkoo() throws ConnectionException {
		super();
		setCountry("United Kingdom");
		setSource("http://www.kelkoo.co.uk/");
		Collection proxies = ProxyFinder.getInstance().getProxies("United Kingdom");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
