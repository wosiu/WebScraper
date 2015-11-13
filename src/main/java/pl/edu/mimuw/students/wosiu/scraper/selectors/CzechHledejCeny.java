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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


// TODO Freshy: english (widok 1 = product view, widok 2 = offers view)
/*
Algorytm:
1. Pobiera liste elementow z widoku 1
2. Na podstawie powyzszego produkty.
 */
public class CzechHledejCeny extends DELabProductSelector {

	public CzechHledejCeny() throws ConnectionException {
		super("Czech Republic", "http://hledejceny.cz");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		final String encoded = getSourceURL() + "/?s=" +
				Utils.urlEncode(product.toLowerCase().trim().replaceAll(" ", "+"));
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		final List<URL> urls = new LinkedList<>();
		final Elements elements = document.getElementsByClass("product");

		for (Element element : elements) {
			try {
				final String href = element.getElementsByClass("price-box").first().child(1).child(1).attr("href");
				urls.add(new URL(href));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}
		return urls;
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();

		if (!document.toString().contains("Nebyly nalezeny žádné produkty s názvem")) {
			//logika dla pobierania dla widoku 1 (linki bezposrednie zamiast linkow do widoku 2)
			Elements elements = document.select("div.itemcell");
			try {
				Date date = new Date();
				for (Element element : elements) {
					products.add(buildProductResultDirectLink(element, date));
				}
			} catch (NullPointerException e) {
				logger.warn(e.getMessage());
			}

			//logika dla pobierania dla widoku 2
			elements = document.select("div.item.first");
			try {
				String product = document.getElementById("prodname").text().trim();
				Date date = new Date();
				for (Element element : elements) {
					products.add(buildProductResult(element, product, date));
				}
			} catch (NullPointerException e) {
				logger.warn(e.getMessage());
			}
		}


		return products;
	}

	private ProductResult buildProductResult(Element element, String productName, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(productName);
		product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());
		return product;
	}

	public URL getShopURL(Element element) {
		return followUrl(element.getElementsByClass("pricevat").first().child(0).attr("href"));
	}

	private String getPrice(Element element) {
		return element.getElementsByClass("pricevat").first().child(0).text();
	}

	private ProductResult buildProductResultDirectLink(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURLDirectLink(element);
		product.setCountry(getCountry());
		product.setPrice(getPriceDirectLink(element));
		product.setProduct(getProductNameDirectLink(element));
		product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());
		return product;
	}

	private Object getPriceDirectLink(Element element) {
		return element.getElementsByClass("search-item-name-headline").first().child(0).text();
	}

	private String getProductNameDirectLink(Element element) {
		return element.getElementsByClass("pricevat").first().child(0).text();
	}

	private URL getShopURLDirectLink(Element element) {
		return followUrl(element.getElementsByClass("pricevat").first().child(0).attr("href"));
	}
}
