package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Algorytm:
 * 1. Z widoku pierwszego wybieramy pierwszą wyszukaną pozycję, przechodzimy do widoku 2.
 * 2. Pobieramy wszystkie pozycje z widoku 2.
 */
public class PolandCeneo extends DELabProductSelector {

	public PolandCeneo() throws ConnectionException {
		super("Poland", "http://ceneo.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String encoded = getSourceURL() + ";szukaj-"
				+ Utils.urlEncode(product.toLowerCase().trim()).replaceAll(" ", "+");
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}

	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		final Document doc = super.download(userAgent, targetURL);
		if (doc.toString().contains("Niestety nic nie znaleziono")) {
			logger.warn("Nie znaleziono produktu w PolandCeneo");
			return null;
		} else {
			String atr = doc.select("div[data-pid]").first()
					.getElementsByClass("btn-compare-outer")
					.select("a[href]")
					.get(0).attr("href");
			return super.download(userAgent, Utils.stringToURL(getSourceURL().toString() + atr));
		}
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();
		if (document != null) {
			final Elements elements = document
					.select("tr[data-offer-price]");

			Date date = new Date();
			for (Element element : elements) {
				if (isProperElement(element)) {
					products.add(buildProductResult(element, date));
				}
			}
		}

		return products;
	}

	private boolean isProperElement(Element element) {
		return element.select("div.product-name").size() > 0;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
        product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(getProduct(element));
        product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
        product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());

		return product;
	}

	public URL getShopURL(Element element) {
		return followUrl(getSourceURL() + element.select("a[href]").first().attr("href"));
	}

	private String getProduct(Element element) {
		return element.select("div.product-name").get(0).getElementsByClass("short-name__txt").text();
	}

	private String getPrice(Element element) {
		return element.attr("data-offer-price") + "PLN";
	}

}
