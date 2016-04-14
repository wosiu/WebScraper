package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Algorytm:
 * 1. Z widoku pierwszego wybieramy liste
 * 2. Pobieramy wszystkie pozycje z widoku 2.
 */
public class AlexaTesco extends Selector {

	public AlexaTesco() throws ConnectionException {
		setCountry("Poland");
		setSource("http://ezakupy.tesco.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		//product = Utils.urlEncodeSpecial(product, '"', '<', '>', '_', '~', '+');
		product = Utils.urlEncode(product, '"', '<', '>', '_', '~', '+');
		String encoded = getSourceURL() + "pl-PL/Search/List?searchQuery="
				+ product + "&SortBy=Relevance&viewType=List";

		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}

	@Override
	public List getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();
		Elements elements = document.select("ul.products > li > div.product");

		for (Element element : elements) {
			final ProductResult product = new ProductResult();

			String priceStr = element.select("span.linePrice").text();
			priceStr = priceStr.replace(',', '.').replace("zł", "");
			float price = Float.parseFloat(priceStr);
			product.setPrice(price);

			String priceAbbrStr = element.select("span.linePriceAbbr").text();
			String[] priceAbbrSpl = priceAbbrStr.replaceAll("[()]", "").replace(',', '.').split(" ");
			float priceAbbr = Float.parseFloat(priceAbbrSpl[0]);
			product.setPriceAbbr(priceAbbr);
			String[] currUnit = priceAbbrSpl[1].split("/");
			product.setCurrency("zł"); // currUnit[0]
			product.setUnit(currUnit[1]);

			Element a = element.select("div.description a").first();
			String name = a.text();
			product.setProduct(name);

			//TODO
//			System.out.println(a.select("img").first());
//			String imgHref = element.select("img[id^=productImg]").attr("src");
//			product.setImage(imgHref);
			String href = a.attr("abs:href");
			product.setShopURL(href);
			product.setShop("Tesco");
			products.add(product);
		}

		return products;
	}
}
