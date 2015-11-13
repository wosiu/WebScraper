package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author w
 */
public class PortugalBuscape extends DELabProductSelector {

	public PortugalBuscape() throws ConnectionException {
		super("Portugal", "http://www.buscape.com.br/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		// remove special chars from product
		product = Utils.normalize(product);
		product = Utils.stripNonEnglish(product);
		product = product.replaceAll(" ", "+");

		String target = getSourceURL().toString() + product;
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		// product search view
		for (Element element :
				document.select("section.proc-search-results > ul.bp-product-list > li.offer")) {

			ProductResult result = new ProductResult();
			String price = element.select("div.single-price > a[href].price > span.value").first().text();
			result.setPrice(price);

			Element a = element.select("div.details > a[href]").first();
			String shopname = a.select("img[alt]").first().attr("alt");
			result.setShop(shopname);

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = element.select("div.description > a.track_checkout").first().text();
			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		// offer view
		for (Element element : document.select("div.offers-list ul > li[log_id]")) {
			ProductResult result = new ProductResult();

			Element a = element.select("a[href].price__link").first();

			String price = a.text();
			result.setPrice(price);

			String shopname = element.select("img[alt].store-logo").first().attr("alt");
			result.setShop(shopname);

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = element.select("img[alt].bp_prevent_error").first().attr("alt");
			result.setProduct(prod);

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate.
	 * Collect urls to offers view from product view.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();

		for (Element element :
				document.select("ul.bp-product-list > li.unique-product > div.actions > a[href]:not(.track_checkout)")) {
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
