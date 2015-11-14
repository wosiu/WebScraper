package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorytm:
 * 1. Z widoku 1 pobieram odnośniki do list z widoku 2.
 * 2. Z powyższego produkty.
 */
public class EstoniaHinnavaatlus extends DELabProductSelector {

	public EstoniaHinnavaatlus() throws ConnectionException {
		super("Estonia", "http://hinnavaatlus.ee");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		final String encoded = getSourceURL() + "/search/?Type=products&Query=" +
				Utils.urlEncode(product.toLowerCase()).trim().replaceAll(" ", "+");
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		final List<URL> urls = new LinkedList<>();
		final Elements elements = document.getElementsByClass("product-list-item");

		for (Element element : elements) {
			try {
                Elements select = element.select("p.product-list-price-line");
                if (!select.isEmpty()) {
                    final String href = select.first().child(0).attr("abs:href");
                    urls.add(new URL(href));
                }
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}
		return urls;
	}

	@Override
	public Object getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();
		final String source = document.toString();
		if (!source.contains("Ühtegi toodet ei leitud.") && !source.contains("Otsing")) {
			String title =
					document.select("h2.section-title.blue.no-border.regular.big.no-transform.margin").first().text();
			Elements elements = document.select("div.col-1-1.extra-offers-offer.opened.bg-white");

            Date date = new Date();
            for (Element element : elements) {
                try {
					products.add(buildProductResult(element, title, date));
                } catch (NullPointerException e) {
                    logger.warn(e.getMessage());
				}
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
		product.setProxy(getLastUsedProxy());
		if (shopURL != null) {
			product.setShopURL(shopURL.toString());
			product.setShop(shopURL.getHost());
		}
		product.setTime(date.getTime());
		return product;
	}

	private String getPrice(Element element) {
		return element.select("p[data-price]").first().text();
	}

	private URL getShopURL(Element element) {
		URL res = null;
		try {
			res = Utils.stringToURL(element.select("li.extra-offers-actions-item").first().child(0).attr("href"));
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return res;
	}
}
