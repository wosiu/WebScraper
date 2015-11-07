package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/*
Algorytm:
1. Pobiera liste elementow z widoku 1
2. Na podstawie powyzszego produkty.
 */
public class CzechHledejCeny extends Selector {

	public CzechHledejCeny() throws ConnectionException {
		super();
		setCountry("Czech Republic");
		setSource("http://hledejceny.cz");
		Collection proxies = ProxyFinder.getProxies("Czech");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		return Utils.stringToURL(getSourceURL() + "/?s=" + product.toLowerCase().trim().replaceAll(" ", "+"));
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

		final Elements elements = document.select("div.item.first");

		try {
			String product = document.getElementById("prodname").text().trim();
			Date date = new Date();
			for (Element element : elements) {
				products.add(buildProductResult(element, product, date));
			}
		} catch (NullPointerException e) {
			logger.warn(e.getMessage());
		}

		return products;
	}

	private ProductResult buildProductResult(Element element, String productName, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry("Czech");
		product.setPrice(getPrice(element));
		product.setProduct(productName);
		product.setSearcher("HledejCeny");
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime(date.getTime());
		return product;
	}

	public URL getShopURL(Element element) {
		return Utils.getRedirectUrl(element.getElementsByClass("pricevat").first().child(0).attr("href"));
	}


	private String getPrice(Element element) {
		return element.getElementsByClass("pricevat").first().child(0).text();
	}
}
