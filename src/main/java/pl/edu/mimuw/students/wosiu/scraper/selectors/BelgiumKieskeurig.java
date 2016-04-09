package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.edu.mimuw.students.wosiu.scraper.*;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author w
 */
public class BelgiumKieskeurig extends DELabProductSelector {

	public BelgiumKieskeurig() throws ConnectionException {
		super("Belgium", "http://kieskeurig.be/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncodeSpecial(product, '~', '<', '>');

		String target = getSourceURL().toString() + "search?q=" + product;
		URL url = Utils.stringToURL(target);
		return url;
	}


	@Override
	public List<ProductResult> getProducts(Document document) {
		List<ProductResult> results = new ArrayList<>();

		// TODO test:
		// http://www.kieskeurig.be/search?q=Fujifilm+X+T10+body
		// http://www.kieskeurig.be/search?q=Converse+Chuck+Taylor+All+Star
		// http://www.kieskeurig.be/systeemcamera/product/2633945-fujifilm-x-t10/prijzen

		// Product view
		for (Element element : document.select("ul#product-listers div.product:not(.js-product):has" +
				"(div.price)")) {
			ProductResult result = new ProductResult();

			String price = element.select("div.price > strong").text();
			result.setPrice(price);

			Element a = element.select("a[href][data-ga]").first();
			if (a == null) {
				logger.warn("Check schema parsing for offer view");
				continue;
			}

			String shopname = UNKNOWN;
			try {
				JSONParser jsonParser = new JSONParser();
				String json = a.attr("data-ga");
				JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
				jsonObject = (JSONObject) jsonObject.get("ecommerce");
				shopname = (String) jsonObject.get("label");
			} catch (ParseException e ) {
				logger.warn("Cannot get shop name");
				continue;
			}

			result.setShop(shopname);

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			String prod = a.select("span").text();
			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}


		// Offer view
		Element prodName = document.select("div.product-detail-container h1[itemprop=name]").first();

		if (prodName == null) {
			// this is not offerts list
			return results;
		}

		String prod = prodName.text();

		// div.price-row:not([class*=advert]) = all div with class prive-row, but without classes matching *advert*
		for (Element element : document.select("form.product-prices > div.price-row:not([class*=advert])")) {
			ProductResult result = new ProductResult();

			String price = element.select("div.prices > span.price-delivered").first().text();
			result.setPrice(price);

			String shopname = element.select("div.shop-info > div.shop-logo > img[alt]").first().attr("alt");
			result.setShop(shopname);

			Element a = element.select("div.price-row > a[href]").first();

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			result.setProduct(prod);
			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

		return results;
	}

	/**
	 * Do not paginate. Collect links from first page.
	 *
	 * @param document
	 * @return
	 */
	@Override
	public List<URL> getNextPages(Document document) {
		List<URL> urls = new ArrayList<>();

		for (Element element : document.select("ul#product-listers div.product.js-product:has(div" +
				".price > a[href])")) {
			String str = element.select("div.product > a[href]").first().attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {
			}
		}
		logger.debug("Collected " + urls.size() + " urls to visit");
		return urls;
	}
}
