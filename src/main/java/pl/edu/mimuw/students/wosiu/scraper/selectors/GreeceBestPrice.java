package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorytm:
 * 1. Z widoku 1 pobieram odnośniki do widoku 2.
 * 2. Z widoku 2 linki do produktów.
 */
public class GreeceBestPrice extends DELabProductSelector {

	public GreeceBestPrice() throws ConnectionException {
		super("Greece", "http://www.bestprice.gr/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {

		product = Utils.urlEncode(product);

		String target = getSourceURL() + "search?q=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());
		final List<URL> urls = new LinkedList<>();

		Elements elements = document.select("table.products > tbody > tr > td > div.info > p.stores >" +
				" a[href]:has(strong)");

		for (Element element : elements) {
			final String href = element.attr("abs:href");
			try {
				urls.add(new URL(href));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}

		return urls;
	}

	@Override
	public Object getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();

		// empty rows has 'td' with class 'store' as well but without 'diff' class
		Elements elements = document.select("tbody.physical-products > tr.paid:has(td.store.diff)");

		for (Element element : elements) {
				products.add(buildProductResult(element));
		}

		return products;
	}

	private ProductResult buildProductResult(Element element) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(getShopName(element));
		product.setProduct(getProductName(element));
		product.setProxy(getLastUsedProxy());

		return product;
	}

	private String getShopName(Element element) {
		return element.select("td.store a.mbanner").first().attr("title");
	}

	private String getProductName(Element element) {
		return element.select("th.descr a.title.no-img").first().text();
	}

	private String getPrice(Element element) {
		return element.select("a.button.tomer.title.no-img").first().text();
	}

	private URL getShopURL(Element element) {
		URL res = null;
		String href = element.select("th.descr a[href].title.no-img").first().attr("abs:href");
		try {
			res = Utils.stringToURL(href);
		} catch (ConnectionException e) {
			logger.warn(e);
		}
		return res;
	}
}
