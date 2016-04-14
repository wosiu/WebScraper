package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class AlexaFrisco extends Selector {

	public AlexaFrisco() throws ConnectionException {
		setCountry("Poland");
		setSource("http://www.frisco.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = Utils.urlEncodeSpecial(product, '"', '<', '>', '_', '~', '+');
		String encoded = getSourceURL() + String.format("q,%s/stn,searchResults", product);

		return Utils.stringToURL(encoded);
	}

	@Override
	public List getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List <ProductResult> products = new LinkedList<>();
		final Elements elements = document.select("div.product-box");

		for (Element element : elements) {
			ProductResult product = new ProductResult();
			final String num = element.select("span.price_num").first().text();
			final String dec = element.select("span.price_decimals").first().text();
			String priceStr = num + "." + dec;
			float price = Float.parseFloat(priceStr);
			product.setPrice(price);

			product.setCurrency("zł"); // currUnit[0]
			String amountUnit = element.select("em").first().text();
			Utils.NumUnits amountConv = Utils.convertToSI(amountUnit);
			if (amountConv != null) {
				product.setUnit(amountConv.unit);
				product.setAmount(amountConv.number);
				product.setPriceAbbr(price / amountConv.number);
			}

			final Element href = element.select("a[href]").first();
			String name = href.attr("title");
			product.setProduct(name);
			product.setShopURL(href.attr("abs:href"));
			product.setShop("Frisco");

			product.setImage(element.select("img[src]").first().attr("src"));
			products.add(product);
		}
		return products;
	}

	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}
}