package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author w
 */
public class LatviaCsv extends Selector {

	public LatviaCsv() throws ConnectionException {
		super();
		setCountry("Latvia");
		setSource("http://www.csv.lv/");
		Collection proxies = ProxyFinder.getInstance().getProxies("Latvia");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncode(product);

		String target = getSourceURL().toString() + "search?q=" + product + "#sort=3";
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		// TODO - use API?
		List<ProductResult> results = new ArrayList<>();

		System.out.println(document);
		// non-featured when record inactive
		for (Element element : document.select("div#listing")) {
			System.out.println("----------");
			System.out.println(element);
			ProductResult result = new ProductResult();
			String price = element.select("div:eq(3) span.price-red").first().text();
			result.setPrice(price);

			String shopname = element.select("div:eq(0)").first().text();
			result.setShop(shopname);

			Element a = element.select("div:eq(0) a[href]").first();

			String link = a.attr("abs:href");
			String redirected = Utils.getRedirectUrl(link).toString();
			result.setShopURL((redirected != null) ? redirected : link);

			String prod = element.select("div:eq(2)").first().text();
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
	 * Warning for future work: pagination made in java-script.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();

		logger.debug("Do not collecting urls to visit");
		return urls;
	}
}
