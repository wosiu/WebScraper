package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author w
 */
public abstract class PricespySelector extends DELabProductSelector {

	public PricespySelector(String country, String source) throws ConnectionException {
		super(country, source);
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncode(product);

		String target = getSourceURL().toString() + "search.php?s=" + product + "#t-product";
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		document.setBaseUri(getSourceURL().toString());

		// non-featured when record inactive
		for (Element element : document.select("table#prislista tbody > tr:not(.non-featured)")) {
			ProductResult result = new ProductResult();
			String price = element.select("td:eq(4)").first().text();
			result.setPrice(price);

			String shopname = element.select("td:eq(0)").first().text();
			result.setShop(shopname);

			Element a = element.select("td:eq(8) > a[href]").first(); //FIX

			String link = a.attr("abs:href");
			String redirected = Utils.getRedirectUrl(link).toString();
			result.setShopURL((redirected != null) ? redirected : link);

			String prod = a.attr("title");
			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate. Collect links (max 12) from first page if product name is too generall.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();
		document.setBaseUri(getSourceURL().toString());

		final int MAX_PRODUCTS = 12;
		Elements elements = document.select("table#table_produktlista > tbody > tr:lt( " + MAX_PRODUCTS + ") > td > " +
				"a[href].price");

		for (Element element : elements) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}
		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
