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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author w
 */
public class BelgiumKieskeurig extends DELabProductSelector {

	public BelgiumKieskeurig() throws ConnectionException {
		super("Belgium", "http://kieskeurig.be/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncode(product);

		String target = getSourceURL().toString() + "search?q=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		Element prodName = document.select("div.product-detail-container h1[itemprop=name]").first();

		if (prodName == null) {
			// this is not offerts list
			return results;
		}

		String prod = prodName.text();

		// div.price-row:not([class*=advert]) = all div with class prive-row, but without classes matching *advert*
		for (Element element : document.select("form.product-prices > div.price-row:not([class*=advert])")) {
			ProductResult result = new ProductResult();

			String price = element.select("div.prices > span.price-delivered").first().text();
			result.setPrice(price);

			String shopname = element.select("div.shop-info > div.shop-logo > img[alt]").first().attr("alt");
			result.setShop(shopname);

			Element a = element.select("div.price-row > a[href]").first();

			String link = a.attr("abs:href");
			String redirected = Utils.getRedirectUrl(link).toString();
			result.setShopURL((redirected != null) ? redirected : link);

			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate. Collect links from first page.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();
		document.setBaseUri(getSourceURL().toString());

		for (Element element : document.select("ul#product-listers div.product:has(div.price > a[href])")) {
			String str = element.select("div.product > a[href]").first().attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}
		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
