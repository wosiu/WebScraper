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

import javax.rmi.CORBA.Util;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class GermanyPreisvergleich extends DELabProductSelector {

	public GermanyPreisvergleich() throws ConnectionException {
		super("Germany", "http://preisvergleich.de/");
	}

	/**
	 * Prepare first url to visit for given product.
	 * Output e.g. http://www.preisvergleich.de/search/result/query/xbox+one/
	 *
	 * @param product
	 * @return
	 * @throws ConnectionException
	 */
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = product.toLowerCase().trim().replaceAll(" ", "+");

		String target = getSourceURL().toString() + "search/result/query/" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		// Product view
		for (Element element : document.select("div#productView > div.jsConversionTag")) {
			ProductResult result = new ProductResult();
			Element a = element.select("a[href].productName").first();

			result.setProduct(a.text());
			String href = a.attr("abs:href");
			result.setSearchURL(followUrl(href).toString());

			result.setPrice(element.select("a.productPrice").first().text());
			result.setShop(element.select("img[name].productShopImg").first().attr("name"));

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		// Offer view
		for (Element element : document.select("ul.productOffers > li.box")) {
			ProductResult result = new ProductResult();
			Element a = element.select("a[href].productPrice").first();

			result.setProduct(element.select("div.description > p:eq(0)").first().text());
			String href = a.attr("abs:href");
			result.setSearchURL(followUrl(href).toString());

			result.setPrice(a.text());
			result.setShop(element.select("div.merchant > img[alt]").first().attr("alt"));

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();

		String nextStrUrl = null;

		// Pagination
		/*URL res;
		try {
			Elements elements = document.getElementsByClass("next");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
		} catch (NullPointerException e) {
			return null;
		}

		try {
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			logger.debug(e.toString());
			return null;
		}
		urls.add(res);*/

		for (Element element : document.select("div#productView > div.productCompare")) {
			String href = element.select("a[href].buttonRetail").first().attr("abs:href");
			try {
				urls.add(Utils.stringToURL(href));
			} catch (ConnectionException e) {
			}
		}

		return urls;
	}


	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		// 1. dla linka /search/result/query/
		// wchodzimy w pierwszy kafelek dot produktu, ale taki który nie ma "bei:", bo te przekierowuja na zewntarz
		// 2. sprawdzamy czy ten kafelek, w ktory wchodzim to dobry produkt?
		// 3. zwracamy document jesli link to /produkt/

		//..ale czyzby? search/result/query/U2,+'Songs+of+Innocence'/ nie ma wynikow nie przekierowujacych na strony
		// zewnetrzne, szukac w tym widoku?
		return super.download(userAgent, targetURL);
	}
}
