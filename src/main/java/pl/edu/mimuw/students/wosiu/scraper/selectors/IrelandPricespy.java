package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class IrelandPricespy extends PricespySelector {

	public IrelandPricespy() throws ConnectionException {
		super();
		setCountry("Ireland");
		setSource("http://www.pricespy.ie/");
		Collection proxies = ProxyFinder.getProxies("Ireland");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
