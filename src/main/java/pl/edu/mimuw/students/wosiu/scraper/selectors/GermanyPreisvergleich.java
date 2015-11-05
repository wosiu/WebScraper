package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class GermanyPreisvergleich extends Selector {

	public GermanyPreisvergleich() throws ConnectionException {
		super();
		setCountry("Germany");
		setSource("http://preisvergleich.de/");
		Collection proxies = ProxyFinder.getProxies("Germany");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
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
		String target = getSourceURL().toString() + "search/result/query/" +
				product.toLowerCase().trim().replaceAll(" ", "+");
		URL url = Utils.stringToURL(target);
		return url;
	}

	@Override
	public List<Object> getProducts(Document document) {
		List<Object> asd = new LinkedList<>();
		asd.add(document.toString().substring(0,30));

		return asd;
	}

	/**
	 * Find url in pagination list for the next page. If does not exist return null.
	 * @param document
	 * @return
	 */
	@Override
	public URL getNextPage(Document document) {
		document.setBaseUri("http://www.preisvergleich.de/");

		String nextStrUrl = null;
		URL res;

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
		return res;
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
