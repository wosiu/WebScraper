package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CroatiaJeftinije extends Selector {

	public CroatiaJeftinije() throws ConnectionException {
		super();
		setCountry("Croatia");
		setSource("http://www.jeftinije.hr/");
		Collection proxies = ProxyFinder.getProxies("Croatia");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}

	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		final Document doc = super.download(userAgent, targetURL);

		logger.debug(doc.toString());
		String atr = doc.getElementsByClass("innerProductBox").first()
				.child(2).child(2)
				.select("a[href]")
				.get(0).attr("href");
		return super.download(userAgent, Utils.stringToURL(atr));
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		return Utils.stringToURL(
				getSourceURL() + "Trazenje/Proizvodi?q=" + product.toLowerCase().trim().replaceAll(" ", "+")
		);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();
		final Elements elements = document.getElementsByClass("offerByBrandB");

		String product = document.select("h1[itemprop]").first().text();
		Date date = new Date();
		for (Element element : elements) {
			products.add(buildProductResult(element, product, date));
		}

		return products;
	}

	private ProductResult buildProductResult(Element element, String productName, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry("Croatia");
		product.setPrice(getPrice(element));
		product.setProduct(productName);
		product.setSearcher("Jeftinije");
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime(date.getTime());
		return product;
	}

	public URL getShopURL(Element element) {
		return Utils.getRedirectUrl(element.select("a[href]").first().attr("href"));
	}


	private String getPrice(Element element) {
		return element.getElementsByClass("price").first().child(0).text();
	}

	@Override
	public Document read(HttpURLConnection connection) throws IOException {
		Document doc = super.read(connection);

		if (doc.toString().contains("captcha")) { //TODO poprawic
			throw new IOException("captcha occured");
		}

		return doc;
	}
}
