package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author w
 */
public abstract class KelkooSelector extends DELabProductSelector {

	public KelkooSelector(String country, String source) throws ConnectionException {
		super(country, source);
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncode(product);

		String target = getSourceURL().toString() + "ctl/do/search?siteSearchQuery=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		if (!document.select("selection.noexactmatch").isEmpty()) {
			logger.debug("Empty search page occurred");
			return Arrays.asList();
		}
		List<ProductResult> results = new ArrayList<>();

		for (Element element : document.select("section[role=main].od-main > div.od-results > div.result.js-result")) {
			ProductResult result = new ProductResult();

			String price = element.select("p.price > strong.value").first().text();
			result.setPrice(price);

			String shopname = element.select("p.merchant-name").first().text();
			result.setShop(shopname);

			String prod = element.select("h3.result-title").text();
			result.setProduct(prod);

			String link = element.select("a[href].result-link").first().attr("href");
			result.setShopURL(followUrl(link).toString());

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate. Collect links from first page if product name is too generall.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List <URL> urls = new ArrayList<>();
		document.setBaseUri(getSourceURL().toString());
		for ( Element element : document.select(
				"div.result > div.result-link > div.total-offers > a[href]") ) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {}
		}

		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
