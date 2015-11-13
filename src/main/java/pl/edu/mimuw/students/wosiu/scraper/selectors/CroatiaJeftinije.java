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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class CroatiaJeftinije extends DELabProductSelector {

	public CroatiaJeftinije() throws ConnectionException {
		super("Croatia", "http://www.jeftinije.hr/");
	}

	@Override
	public Document download(String userAgent, URL targetURL) throws ConnectionException {
		final Document doc = super.download(userAgent, targetURL);

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
		final List<URL> urls = new LinkedList<>();
		final Elements elements = document.getElementsByClass("innerProductBox");

		for (Element element : elements) {
			try {
				urls.add(new URL(element.child(2).child(2)
						.select("a[href]")
						.get(0).attr("href")));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}
		return urls;
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
        product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(productName);
        product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setTime(date.getTime());
		return product;
	}

	public URL getShopURL(Element element) {
		return followUrl(element.select("a[href]").first().attr("href"));
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
