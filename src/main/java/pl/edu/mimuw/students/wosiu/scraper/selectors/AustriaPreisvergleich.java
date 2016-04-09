package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
				Utils.urlEncodeSpecial(product, '~', '"', '<', '>');
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());

		List<URL> res = new LinkedList<>();

		final Elements select = document.select("div.article.toshop.toarticle div.float-left.full div.buttonholder a:not([c])");
		List<Element> elements = Arrays.asList(select.toArray(new Element[select.size()]));

		if (elements.size() > 20) {
			elements = elements.subList(0, 20);
		}
		for (Element element : elements) {
			try {
				res.add(Utils.stringToURL(element.attr("abs:href")));
			} catch (ConnectionException e) {
				e.printStackTrace();
			}
		}


		return res;
	}

	@Override
	public List getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();

		Elements select = document.select("div.wrapper.gallery div.article");

		List<Element> elements = Arrays.asList(select.toArray(new Element[select.size()]));
		if (elements.size() > 20) {
			elements = elements.subList(0, 20);
		}
		Date date = new Date();
		//1st view
		for (Element element : elements) {
			products.add(buildProductResult(element, date));
		}

		//2nd view
		elements = document.select("div.subitem div.article");
		for (Element element : elements) {
			ProductResult product = new ProductResult();
			product.setCountry(getCountry());
			product.setPrice(element.select("div.float-left.price span.highlightprice:not(.smaller) span.units").text() + " EUR");
			product.setProduct(element.select("div.namedelivery span.larger a[href]").text());
			product.setSearcher(getSourceURL().toString());
			product.setShopURL(element.select("div.namedelivery span.larger a[href]").attr("abs:href"));
			String shop = element.select("div.image img.shoplogo").attr("alt");
			if (shop == null || shop.isEmpty()) {
				shop = element.select("div.image span.textshoplogo").text();
			}
			product.setShop(shop);
			product.setTime(date.getTime());
			product.setProxy(getLastUsedProxy());
			products.add(product);
		}


		return products;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(getProduct(element));
		product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(getShopName(element));
		product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());
		return product;
	}

	private String getShopName(Element element) {
		return element.select("a.shoplogo").text();
	}

	private URL getShopURL(Element element) {
		return followUrl(
				element.select("div.namedescription a[href]").attr("abs:href")
		);
	}

	private String getProduct(Element element) {
		return element.select("div.namedescription span.larger a[href]").text();
	}

	private String getPrice(Element element) {
		return element.select("div.price span.highlightprice span.units").text() + " EUR";
	}
}
