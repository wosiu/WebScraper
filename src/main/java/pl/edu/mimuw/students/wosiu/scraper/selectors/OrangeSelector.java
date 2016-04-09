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
import java.util.Arrays;
import java.util.List;

/**
 * @author w
 */
public abstract class OrangeSelector extends DELabProductSelector {


	public OrangeSelector(String country, String source) throws ConnectionException {
		super(country, source);
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String target = getSourceURL().toString() + "CategorySearch.php?st=" +
				Utils.urlEncodeSpecial(product, '"', '*', '>');
		URL url = Utils.stringToURL(target);
		return url;
	}

	public List<ProductResult> processOfferBox(Document document) {
		Elements elements = document.getElementsByClass("offer-box-container");
		List<ProductResult> results = new ArrayList<>(elements.size());

		for (Element element : elements) {
			ProductResult result = new ProductResult();
			Element money = element.getElementsByClass("price").first();
			String cur = money.child(0).text();
			String price = money.attr("content");
			result.setPrice(price + cur);

			Element title = element.select("h4").first();
			String prod = title.text();
			result.setProduct(prod);

			String prodHref = title.select("a[href]").first().attr("href").toString();
			result.setShopURL(followUrl(prodHref).toString());

			String shopName = element.getElementsByClass("shopname").first().text();
			result.setShop(shopName);

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	@Override
	public List<ProductResult> getProducts(Document document) {

		if (document.select("#micro-data > div.row.category-page-wrapper > div.alert.alert-warning").size() > 0) {
			logger.debug("Empty search page occurred");
			return Arrays.asList();
		}

		return processOfferBox(document);
	}

	/**
	 * Do not paginate. Collect links from first page.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		Elements elements = document.getElementsByClass("product-box-container");
		List<URL> urls = new ArrayList<>(elements.size());
		for (Element element : document.select("div.image-link-container > a[href].image")) {
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
		if (document.select("body").first().children().isEmpty()) {
			throw new IOException("Empty page occured");
		}
		return document;
	}
}