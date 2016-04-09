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

// TODO to english

/**
 * Algorytm:
 * 1. Z widoku 1 pobieram odnośniki do list z widoku 2.
 * 2. Z powyższego produkty.
 */
public class FinlandiaHinta extends DELabProductSelector {

	public FinlandiaHinta() throws ConnectionException {
		super("Finland", "http://hinta.fi");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		// TODO encode fix?
		final String encoded = getSourceURL() + "/haku?q=" +
				Utils.urlStripEncode(product.toLowerCase().trim().replaceAll(" ", "+"));
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		final List<URL> urls = new LinkedList<>();
		final Elements elements = document.select("a[href].hv--product-a");

		for (Element element : elements) {
			try {
				final String href = element.attr("abs:href");
				urls.add(new URL(href));
			} catch (MalformedURLException e) {
				logger.info(e.getMessage());
			}
		}

		return urls;
	}

	@Override
	public List getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();
		document.setBaseUri(getSourceURL().toString());

		Date date = new Date();
		for (Element element : document.select("tr.hv-table-list-tr.hv--offer-list")) {
			final ProductResult result = buildProductResult(element, date);
			if (result.getShopURL() != null && !result.getShopURL().isEmpty()) {
				products.add(result);
			}
		}

		return products;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);

        product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(getProductName(element));
        product.setSearcher(getSourceURL().toString());
		product.setProxy(getLastUsedProxy());

		if (shopURL != null) {
			product.setShopURL(shopURL.toString());
		}
		product.setShop(getShop(element));
		product.setTime(date.getTime());
		return product;
	}

	private String getShop(Element element) {
		String res = element.select("img.hv-store-logo.hvjs-lazy-image").attr("alt");
		if (res == null || res.isEmpty()) {
			res = element.select("th[scope=row] span[itemprop=seller]").text();
		}
		return res;
	}

	private String getProductName(Element element) {
		return element.select("span[itemprop=name]").first().text();
	}

	private String getPrice(Element element) {
		return element.select("span[itemprop=price]").first().text();
	}

	private URL getShopURL(Element element) {
		URL res = null;
		Elements select = element.select("div.hv--name a[rel=nofollow]");
		if (!select.isEmpty()) {
			res = followUrl(select.first().attr("abs:href"));
		}
		return res;
	}
}
