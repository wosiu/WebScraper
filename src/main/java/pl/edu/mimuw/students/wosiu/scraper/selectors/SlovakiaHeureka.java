package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author w
 */
public class SlovakiaHeureka extends DELabProductSelector {

	public SlovakiaHeureka() throws ConnectionException {
		super("Slovakia", "http://www.heureka.sk/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncodeSpecial(product, '~', '"', '<', '>');
		String target = getSourceURL().toString() + "?h%5Bfraze%5D=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		// offerts view:
		for (Element element : document.select("div.shopspr div.shoppr")) {
			ProductResult result = new ProductResult();

			result.setPrice(element.select("div.pr > p").first().text());

			Element a = element.select("div.buy > p:eq(1) > a[href]").first();
			result.setShop(a.text());

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			result.setProduct(element.select("div.desc > p.js-desc-paragraph").first().text());

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		// products view
		for (Element element : document.select("div#fulltext > div.product")) {
			ProductResult result = new ProductResult();

			result.setPrice(element.select("p.price").first().text());

			result.setShop(element.select("p.shop-name").first().text());

			Element a = element.select("div.desc > h2 > a[href]").first();
			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			result.setProduct(a.text());
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Take links from results and do pagination (max 7 times).
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		// TODO

		List<URL> urls = new ArrayList<>();

		for (Element element : document.select("div#search > div.product > div.wherebuy")) {
			String str = element.select("a[href]").first().attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str + "?expand=1"));
			} catch (ConnectionException e) {
			}
		}

		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
