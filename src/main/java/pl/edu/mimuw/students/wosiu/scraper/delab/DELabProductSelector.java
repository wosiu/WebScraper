package pl.edu.mimuw.students.wosiu.scraper.delab;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Gatherproxy;

import java.util.Collection;

public abstract class DELabProductSelector extends Selector {
	public DELabProductSelector(String country, String source) throws ConnectionException {
		super();
		setCountry(country);
		setSource(source);

		if (!"Poland".equals(country)) {
			ProxyFinder.getInstance().addProxySelector(new Gatherproxy(country));
			Collection proxies = ProxyFinder.getInstance().getProxies(country);
			if (proxies == null || proxies.isEmpty()) {
				logger.info(country + ": No proxy in ProxyFinder");
			} else {
				logger.info(country + ": " + proxies.size() + " proxy servers found");
				addAllProxies(proxies);
			}
		}
	}
}
