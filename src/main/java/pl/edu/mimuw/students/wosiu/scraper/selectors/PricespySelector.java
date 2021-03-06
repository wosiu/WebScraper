package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
		product = Utils.urlEncode(product, '(', ')', '~', '\'').replaceAll("\\+", "%20");

		String target = getSourceURL().toString() + "search.php?s=" + product + "#t-product";
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		document.setBaseUri(getSourceURL().toString());

		// Offer view
		// non-featured when record inactive
		for (Element element : document.select("table#prislista tbody > tr:not(.non-featured)")) {
			ProductResult result = new ProductResult();
			String price = element.select("td:eq(4)").first().text();
			result.setPrice(price);

			String shopname = element.select("td:eq(0)").first().text();
			result.setShop(shopname);

			Element a = element.select("td:eq(8) > a[href]").first(); //FIX

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = a.attr("title");
			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		// Product view

		// Note there are tabs represented by divs: #product, #brand_categegory, #raw, ...
		// Sometimes there are some:
		// 1.: http://pricespy.ie/search.php?s=xbox%20one#t-product  we do not collect results from here
		// ..sometimes only one:
		// 2.: http://pricespy.ie/search.php?s=Songs%20of%20Innocence%20-%20U2%20  we collect results
		// we want to collect from such product view only if there are no product tab. We take "uncategorised prices"
		// (div#raw)

		if ( document.select("div#search_results_page > ul:eq(0) > li > a[href]#tab-product").isEmpty() ) {
			Elements elements = document.select("div#raw tr:has(td:eq(0) > a[href])");

			for (Element element : elements) {
				ProductResult result = new ProductResult();

				result.setPrice(element.select("span.price").first().text());

				Element a = element.select("td:eq(0) > a[href]").first();
				String link = a.attr("abs:href");
				String redirected = followUrl(link).toString();

				result.setShopURL(redirected);

				// there is no shop name in structure
				String host = getSourceURL().getHost();
				String domain = host.replaceAll("http://", "").replaceAll("www\\.", "");
				if (redirected.contains(domain)) {
					result.setShop(UNKNOWN);
				} else {
					result.setShop(host);
				}


				String prod = a.text();
				result.setProduct(prod);
				result.setCountry(getCountry());
				result.setProxy(getLastUsedProxy());
				result.setSearcher(getSourceURL().toString());

				results.add(result);
			}
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

		// Product
		Elements elementsProducts = document.select("div#product table#table_produktlista > tbody > tr:lt( " +
				MAX_PRODUCTS + ") > td > a[href].price");

		for (Element element : elementsProducts) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}

		// Books
		Elements elementsBooks = document.select("div#book table#table_book_list > tbody > tr:lt( " +
				MAX_PRODUCTS + ") > td:eq(1) a[href]");

		for (Element element : elementsBooks) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}


		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}

	@Override
	public Document read(HttpURLConnection connection) throws IOException {
		Document document = super.read(connection);
		if ("Authentication Required".equals( document.select("head > title").text() )) {
			throw new IOException("Wrong page occurred, title: Authentication Required");
		}
		return document;
	}
}
