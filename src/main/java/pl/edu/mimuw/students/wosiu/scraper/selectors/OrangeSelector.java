package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author w
 */
public abstract class OrangeSelector extends Selector {

	private static final List<Character> SPECIAL = Arrays.asList('"','*','>');

	private String convert(String str) {
		StringBuilder out = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (SPECIAL.contains(c)) {
				out.append(c);
			} else try {
				out.append(URLEncoder.encode("" + c, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
				out.append(c);
			}
		}
		return out.toString();
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String target = getSourceURL().toString() + "CategorySearch.php?st=" +
				convert(product.trim());
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public List<ProductResult> getProducts(Document document) {
		Elements elements = document.getElementsByClass("offer-box-container");
		List<ProductResult> results = new ArrayList<>(elements.size());

		if (document.select("#micro-data > div.row.category-page-wrapper > div.alert.alert-warning")
				.size() > 0) {
			logger.debug("Empty search page occurred");
			return results;
		}

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
			URL redirectUrl = Utils.getRedirectUrl(prodHref);
			if (redirectUrl != null) {
				prodHref = redirectUrl.toString();
			}
			result.setShopURL(prodHref);

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
	public List<URL> getNextPages(Document document) {
		// do not paginate
		return null;
	}

	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		if (targetURL.toString().contains("/CategorySearch.php?")) {
			// probably need one more step
			Document document = super.download(userAgent, targetURL);
			document.setBaseUri(getSourceURL().toString());
			String nextStrUrl = null;
			try {
				Element box = document.getElementsByClass("product-box-container").first();
				Element next = null;

				Element orange = box.getElementsByClass("button-orange").first();
				Element grey  = box.getElementsByClass("button-grey").first();

				if (grey != null) {
					next = grey.select("a").first();
				} else if (orange != null) {
					next = orange.select("a").first();
				} else {
					// no button
					return document;
				}

				nextStrUrl = next.attr("abs:href");

				if (nextStrUrl.contains("Jump.php")) {
					// link redirects outside searcher
					// so there is no product list for this product
					return document;
				}

			} catch (NullPointerException e) {
				// probably no results
				return super.download(userAgent, targetURL);
			}
			targetURL = Utils.stringToURL(nextStrUrl);
		}
		Document document = super.download(userAgent, targetURL);
		return document;
	}
}
