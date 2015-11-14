package pl.edu.mimuw.students.wosiu.scraper.delab;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Gatherproxy;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

public abstract class DELabProductSelector extends Selector {

	public void setCollectProxy(boolean collectProxy) {
		this.collectProxy = collectProxy;
	}

	public boolean isCollectProxy() {
		return collectProxy;
	}

	public boolean isRedirectShopLink() {
		return redirectShopLink;
	}

	public void setRedirectShopLink(boolean redirectShopLink) {
		this.redirectShopLink = redirectShopLink;
	}

	private boolean collectProxy = false;
	private boolean redirectShopLink = false;


	public DELabProductSelector(String country, String source) throws ConnectionException {
		super();
		setCountry(country);
		setSource(source);

		if (collectProxy && !"Poland".equals(country)) {
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
