package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

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
        product = Utils.normalize(product);
        product = product.toLowerCase();
        // remove !@#$... etc
        product = Utils.stripNonEnglish(product);
        // replace ' ' with '+'
		product = Utils.urlEncode(product);

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

        Date date = new Date();
        JsonParser parser = new JsonParser();
        JsonArray results = (JsonArray) parser.parse(document.select("body").text()).getAsJsonObject().get("result");
        for (JsonElement e : results.getAsJsonArray()) {
            products.add(buildProductResult(e.getAsJsonObject(), date));
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
