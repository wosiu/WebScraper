package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorytm:
 * 1. Z widoku 1 pobieram odnośniki do widoku 2.
 * 2. Z widoku 2 linki do produktów.
 */
public class GreeceBestPrice extends DELabProductSelector {

	public GreeceBestPrice() throws ConnectionException {
		super("Greece", "http://www.bestprice.gr/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {

		product = Utils.urlEncodeSpecial(product);

		String target = getSourceURL() + "search?q=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}

	/**
	 * Do not paginate
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());
		final List<URL> urls = new LinkedList<>();

		// Note there are 2 products view:
		// http://www.bestprice.gr/search?q=oxford+wordpower
		// http://www.bestprice.gr/search?refqid=34EynSo_mr5_ba736&q=xbox+one
		Elements elements = document.select("div#results-main > table.products > tbody > tr > td > div.info > " +
				"p.stores" +
				" > a[href]:not(.tomer):has(strong)");

		for (Element element : elements) {
			final String href = element.attr("abs:href");
			try {
				urls.add(new URL(href));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}

		return urls;
	}

	@Override
	public List getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();

		// Offers view
		// empty rows has 'td' with class 'store' as well but without 'diff' class
        Elements select = document.select("div#content div#prices tbody.physical-products > tr.paid:has(td" +
				".store" +
				".diff)");

        for (Element element : select) {
			ProductResult product = new ProductResult();

			product.setCountry(getCountry());
			product.setPrice( element.select("a.button.tomer.title.no-img").first().text() );
			product.setSearcher(getSourceURL().toString());
			String href = element.select("th.descr a[href].title.no-img").first().attr("abs:href");
			product.setShopURL( followUrl(href).toString() );
			product.setShop( element.select("td.store a.mbanner").first().attr("title") );
			product.setProduct( element.select("th.descr a.title.no-img").first().text() );
			product.setProxy(getLastUsedProxy());

			products.add(product);
		}

		// Product view
		// Note there are 2 kinds of product view:
		// http://www.bestprice.gr/search?q=oxford+wordpower
		// http://www.bestprice.gr/search?refqid=34EynSo_mr5_ba736&q=xbox+one
		// takes results within both if any
		Elements elements = document.select("div#results-main > table.products > tbody > tr > td" +
		":has(p.stores > a[href]:not(:has(strong))");

        for (Element element : elements) {
            ProductResult product = new ProductResult();

            product.setCountry(getCountry());
            product.setPrice(element.select("div.info p.price a").first().text());
            product.setSearcher(getSourceURL().toString());
            String href = element.select("div.info p.price a[href]").first().attr("abs:href");
            product.setShopURL(followUrl(href).toString());
            product.setShop(element.select("div.info p.stores.clr").first().text());
            product.setProduct(element.select("div.img img[alt]").first().attr("alt"));
            product.setProxy(getLastUsedProxy());

            products.add(product);
        }

		return products;
	}
}
