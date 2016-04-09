package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.google.gson.*;
import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author w
 */
public class LatviaCsv extends DELabProductSelector {

    private String targetUrl;
    private boolean getNextPages = false;

	public LatviaCsv() throws ConnectionException {
		super("Latvia", "http://www.csv.lv/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
        getNextPages = false;
        // change diacritics
        product = Utils.stripAccents(product);
        product = product.toLowerCase();
        // remove !@#$... etc
        product = Utils.stripNonEnglish(product);
        // replace ' ' with '+'
		product = Utils.urlStripEncode(product);

        targetUrl = getSourceURL() + "api/json/?q=" + product;
        URL url = Utils.stringToURL(targetUrl);
		return url;
	}


	@Override
    public List<URL> getNextPages(Document document) {
        List<URL> urls = new ArrayList<>();
        if (!getNextPages) {
            for (int i = 1; i <= 7; i++) {
                try {
                    urls.add(new URL(targetUrl + "&page=" + String.valueOf(i)));
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage());
                }
            }
            getNextPages = true;
        }

        return urls;
    }

    @Override
    public List<ProductResult> getProducts(Document document) {
        List<ProductResult> products = new LinkedList<>();

        try { //
            Date date = new Date();
            JsonParser parser = new JsonParser();
            JsonArray results = (JsonArray) parser.parse(document.select("body").text()).getAsJsonObject().get("result");
            for (JsonElement e : results.getAsJsonArray()) {
                products.add(buildProductResult(e.getAsJsonObject(), date));
            }
        } catch (JsonSyntaxException mue) { //http://www.csv.lv/api/json/?q=armani+emporio+he+100+ml invalid json
            mue.printStackTrace();
        }

        return products;
	}

    private ProductResult buildProductResult(JsonObject object, Date date) {
        final ProductResult product = new ProductResult();
        product.setCountry(getCountry());
        product.setPrice(object.get("price").getAsString() + object.get("currency").getAsString());
        product.setProduct(object.get("name").getAsString());
        product.setSearcher(getSourceURL().toString());
        product.setShopURL(object.get("link").getAsString());
        product.setShop(object.get("website_domain").getAsString());
        product.setTime(date.getTime());
        product.setProxy(getLastUsedProxy());
        return product;
	}

}
