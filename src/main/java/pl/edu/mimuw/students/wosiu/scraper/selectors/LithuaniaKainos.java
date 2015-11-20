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
public class LithuaniaKainos extends DELabProductSelector {

	public LithuaniaKainos() throws ConnectionException {
		super("Lithuania", "http://www.kainos.lt/");
	}

	// rows in search result (there are mixed: links to shops and link to oferts list within keinos)
	private final String PRODUCTS_ROW_QUERY = "table#search_results > tbody > tr > td > a[href].go-to-shop";

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncodeSpecial(product, '~', '"', '<', '>');

		String target = getSourceURL().toString() + "lt/search?search_query=" + product + "&submit_search=Ieškoti";
		URL url = Utils.stringToURL(target);
		return url;
	}

	private final String SHOP_NAME_PREFIX = "Pardavėjas: ";

	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		document.setBaseUri(getSourceURL().toString());

		// offerts view:
		for (Element element : document.select("table.compare > tbody > tr.price-row")) {
			ProductResult result = new ProductResult();

			Element a = element.select("td:eq(2) > a[href]").first();

			String price = a.select("span.price").first().ownText();
			result.setPrice(price);

			// Note cannot take from img[alt] as sometimes it doesn't occur
			String shopname = element.select("td:eq(0) > a[title].about").first().attr("title");
			shopname = shopname.replaceAll("Apie pardavėją", "").replaceAll("[„“  ]", "").trim();
			result.setShop(shopname);

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = a.select("span.info > strong").text();
			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		// products view
		for (Element element : document.select(PRODUCTS_ROW_QUERY + "[onclick]")) {
			ProductResult result = new ProductResult();

			Element mix = element.select("span.price").first();
			String price = mix.ownText();
			result.setPrice(price);

			String shopname = mix.select("span.compare-other-count").first().text();
			if (shopname.length() <= SHOP_NAME_PREFIX.length()) {
				logger.debug("Skip element - incorrect shop name");
				continue;
			}

			shopname = shopname.substring(SHOP_NAME_PREFIX.length());
			result.setShop(shopname);

			String link = element.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = element.select("span.info > strong").text();
			result.setProduct(prod);
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
		List<URL> urls = new ArrayList<>();

		// Collect rows with links to comparing offerts links
		Elements elements = document.select(PRODUCTS_ROW_QUERY + ":not([onclick])");

		for (Element element : elements) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}

		// Pagination
		final int MAX_PAGE = 7;
		Element next = document.select("a[href].next").first();
		if (next != null) {
			String nextStr = next.attr("href");
			if (!nextStr.contains("page_nr=" + MAX_PAGE)) {
				try {
					urls.add(Utils.stringToURL(nextStr));
				} catch (ConnectionException e) {
				}
			}
		}

		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
