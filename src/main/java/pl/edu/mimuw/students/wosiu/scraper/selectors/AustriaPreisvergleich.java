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
import java.util.*;

/**
 * Algorytm:
 * 1. Pobieramy wszystkie pozycje z widoku 1 z uwzględnieniem paginacji.
 */
public class AustriaPreisvergleich extends DELabProductSelector {

	public AustriaPreisvergleich() throws ConnectionException {
		super("Austria", "http://preisvergleich.at/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String encoded = getSourceURL() + "tag/?tag=" +
				Utils.urlEncode(product.toLowerCase().trim()).replaceAll(" ", "+");
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());

		String nextStrUrl = null;
		List<URL> res = new LinkedList<>();

		try {
			Elements elements = document.getElementsByClass("pag-link");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
			res.add(Utils.stringToURL(nextStrUrl));
		} catch (ConnectionException e) {
			logger.debug(e.toString());
		} catch (NullPointerException e) {
			logger.warn("npe: " + e.getMessage());
		}

		return res;
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();

		if (!document.toString().contains("Ihre Suchanfrage hat keine Ergebnisse erbracht")) {
			final Elements elements = document.getElementsByClass("article");

			Date date = new Date(); //TODO ladniej na czytelny format przerobic (to samo w pl)
			for (Element element : elements) {
				products.add(buildProductResult(element, date));
			}
		}

		return products;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry("Austria");
		product.setPrice(getPrice(element));
		product.setProduct(getProduct(element));
		product.setSearcher("Preisvergleich");
		product.setShopURL(shopURL.toString());
		product.setShop(getShopName(element));
		product.setTime(date.getTime());
		return product;
	}

	private String getShopName(Element element) {
		return element.select("a.shoplogo").first().text();
	}

	private URL getShopURL(Element element) {
		return followUrl(
				getSourceURL() +
						element.getElementsByClass("namedescription").get(0).select("a[href]").get(0).attr("href")
		);
	}

	private String getProduct(Element element) {
		return element.getElementsByClass("namedescription").get(0).select("a[href]").get(0).text();
	}

	private String getPrice(Element element) {
		return element.getElementsByClass("price").get(0).getElementsByClass("units").text() + "EUR";
	}
}
