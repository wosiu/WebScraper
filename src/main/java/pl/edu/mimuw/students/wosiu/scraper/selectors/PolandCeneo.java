package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by maciej on 05.11.15.
 */
public class PolandCeneo extends Selector {

	public PolandCeneo() throws ConnectionException {
		super();
		setCountry("Poland");
		setSource("http://ceneo.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		return Utils.stringToURL(getSourceURL() + ";szukaj-" + product.toLowerCase().trim().replaceAll(" ", "+"));
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());

		String nextStrUrl = null;
		URL res = null;

		try {
			Elements elements = document.getElementsByClass("arrow-next");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			logger.debug(e.toString());
		} catch (NullPointerException e) {

		}

		return Arrays.asList(res);
	}

	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		final Document doc = super.download(userAgent, targetURL);
		String atr = doc.select("div[data-pid]").get(0)
				.getElementsByClass("btn-compare-outer")
				.select("a[href]")
				.get(0).attr("href");
		return super.download(userAgent, Utils.stringToURL(getSourceURL().toString() + atr));
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();
		final Elements elements = document
				.select("tr[data-offer-price]");

		for (Element element : elements) {
			if (isProperElement(element)) {
				products.add(buildProductResult(element));
			}
		}


		return products;
	}

	private boolean isProperElement(Element element) {
		return element.select("div.product-name").size() > 0;
	}

	private ProductResult buildProductResult(Element element) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry("Poland");
		product.setPrice(getPrice(element));
		product.setProduct(getProduct(element));
		product.setSearcher("Ceneo");
		product.setSearchURL(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime();
		return product;
	}

	public URL getShopURL(Element element) {
		return Utils.getRedirectUrl(getSourceURL() + element.select("a[href]").get(0).attr("href"));
	}

	private String getProduct(Element element) {
		return element.select("div.product-name").get(0).getElementsByClass("short-name__txt").text();
	}

	private String getPrice(Element element) {
		return element.attr("data-offer-price") + "PLN";
	}

}
