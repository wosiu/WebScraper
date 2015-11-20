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
import java.util.*;

/**
 * Algorytm:
 * 1. Z widoku pierwszego wybieramy liste
 * 2. Pobieramy wszystkie pozycje z widoku 2.
 */
public class PolandCeneo extends DELabProductSelector {

	public PolandCeneo() throws ConnectionException {
		super("Poland", "http://ceneo.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String encoded = getSourceURL() + ";szukaj-"
				+ Utils.urlEncodeSpecial(product, '!', '(', ')', '-');
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
        document.setBaseUri(getSourceURL().toString());
        List<URL> urls = new LinkedList<>();
        final Elements select = document.select("div[data-pid] div.cat-prod-row-desc strong.cat-prod-row-name a[href]:last-child");
        for (Element element : select) {
            try {
                urls.add(new URL(element.attr("abs:href")));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
		}

        return urls;
	}

	@Override
	public Object getProducts(Document document) {
        document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();
		if (document != null) {
            Elements elements = document
					.select("tr.product-offer.js_product-offer");

			//produkty z widoku 2
			Date date = new Date();
			for (Element element : elements) {
				if (isProperElement(element)) {
					products.add(buildProductResult(element, date));
				}
			}

            //widok "moda"

            elements = document.select("div.grid-item");
            for (Element element : elements) {
                final ProductResult product = new ProductResult();
                product.setCountry(getCountry());
                product.setPrice(getPriceFashion(element));
                final Elements select = element.select("strong.grid-item__name a.go-to-shop.js_conv");
                product.setProduct(select.attr("title"));
                product.setShopURL(followUrl(select.attr("abs:href")).toString());
                product.setSearcher(getSourceURL().toString());
                String shopName = element.select("a.grid-item__thumb.go-to-shop img.grid-item__store").attr("alt");
                if (shopName == null || shopName.isEmpty()) {
                    shopName = "Ceneo";
                }
                product.setShop(shopName);
                product.setTime(date.getTime());
                product.setProxy(getLastUsedProxy());

                products.add(product);
            }
		}

		return products;
	}

    private String getPriceFashion(Element element) {
        return element.select("span.grid-item__price[itemprop=lowPrice]").text();
    }

	private boolean isProperElement(Element element) {
		return element.select("div.product-name").size() > 0;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
        product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setProduct(getProduct(element));
        product.setSearcher(getSourceURL().toString());
        product.setShopURL(getShopURL(element).toString());
        product.setShop(element.select("td.cell-store-logo a.store-logo.go-to-shop img[src]").attr("alt"));
        product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());

		return product;
	}

	public URL getShopURL(Element element) {
		return followUrl(getSourceURL() + element.select("a[href]").first().attr("href"));
	}

	private String getProduct(Element element) {
		return element.select("div.product-name").get(0).getElementsByClass("short-name__txt").text();
	}

	private String getPrice(Element element) {
		return element.attr("data-offer-price") + "PLN";
	}

}
