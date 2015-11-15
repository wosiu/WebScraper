package pl.edu.mimuw.students.wosiu.scraper.delab;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Gatherproxy;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Proxygaz;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public abstract class DELabProductSelector extends Selector {

	public boolean isRedirectShopLink() {
		return redirectShopLink;
	}

	public void setRedirectShopLink(boolean redirectShopLink) {
		this.redirectShopLink = redirectShopLink;
	}

	private boolean redirectShopLink = false;

	// value to set unknown field
	public static final String UNKNOWN = "NULL";

	public DELabProductSelector(String country, String source) throws ConnectionException {
		super();
		setCountry(country);
		setSource(source);
	}

	public void collectProxies() {
		if ("Poland".equals(getCountry())) {
			return;
		}
		ProxyFinder.getInstance().addProxySelector(new Gatherproxy(getCountry()));
		ProxyFinder.getInstance().addProxySelector(new Proxygaz(getCountry()));
		Collection proxies = ProxyFinder.getInstance().getProxies(getCountry());
		if (proxies == null || proxies.isEmpty()) {
			logger.info(getCountry() + ": No proxy in ProxyFinder");
		} else {
			logger.info(getCountry() + ": " + proxies.size() + " proxy servers found");
			addAllProxies(proxies);
		}
	}

	protected URL followUrl(String url) {
		URL org;
		try {
			org = new URL(url);
		} catch (MalformedURLException e) {
			logger.warn("Malformed shop url: " + url);
			return null;
		}
		if (!redirectShopLink) {
			return org;
		}
		URL redirected = Utils.getRedirectUrl(url);
		if (redirected != null) {
			return redirected;
		}
		return org;
	}
}
