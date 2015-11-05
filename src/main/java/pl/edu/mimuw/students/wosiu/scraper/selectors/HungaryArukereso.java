package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class HungaryArukereso extends Selector {

	private static final List<Character> SPECIAL = Arrays.asList('"','*','>');

	private String convert(String str) {
		StringBuilder out = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (SPECIAL.contains(c)) {
				out.append(c);
			} else try {
				out.append(URLEncoder.encode("" + c, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				out.append(c);
			}
		}
		return out.toString();
	}

	public HungaryArukereso() throws ConnectionException {
		super();
		setCountry("Hungary");
		setSource("http://www.arukereso.hu/");
		Collection proxies = ProxyFinder.getProxies("Hungary");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}

	public URL prepareTargetUrl(String product) throws ConnectionException {
		String target = getSourceURL().toString() + "CategorySearch.php?st=" +
				convert(product.trim());
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public List<ProductResult> getProducts(Document document) {
		Elements elements = document.getElementsByClass("offer-box-container");
		List<ProductResult> results = new ArrayList<>(elements.size());

		for (Element element : elements) {
			ProductResult result = new ProductResult();
			Element money = element.getElementsByClass("price").first();
			String cur = money.child(0).text();
			String price = money.attr("content");
			result.setPrice(price + cur);

			Element title = element.select("h4").first();
			String prod = title.text();
			result.setProduct(prod);

			String prodHref = title.select("a[href]").first().attr("href").toString();
			URL redirectUrl = Utils.getRedirectUrl(prodHref);
			if (redirectUrl != null) {
				prodHref = redirectUrl.toString();
			}
			result.setShopURL(prodHref);

			String shopName = element.getElementsByClass("shopname").first().text();
			result.setShop(shopName);

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri("http://www.preisvergleich.de/");

		String nextStrUrl = null;
		URL res;

		try {
			Elements elements = document.getElementsByClass("next");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
		} catch (NullPointerException e) {
			return null;
		}

		try {
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			logger.debug(e.toString());
			return null;
		}
		return Arrays.asList(res);
	}


	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		if (targetURL.toString().contains("/CategorySearch.php?")) {
			// one more step
			Document document = super.download(userAgent, targetURL);
			document.setBaseUri(getSourceURL().toString());
			String nextStrUrl = null;
			try {
				Elements elements = document.getElementsByClass("button-orange");
				Element next = elements.first().select("a").first();
				nextStrUrl = next.attr("abs:href");
			} catch (NullPointerException e) {
				// probably no results
				return super.download(userAgent, targetURL);
			}
			targetURL = Utils.stringToURL(nextStrUrl);
		}
		return super.download(userAgent, targetURL);
	}
}
