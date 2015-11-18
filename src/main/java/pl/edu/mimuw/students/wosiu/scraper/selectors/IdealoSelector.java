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

// TODO comments to english
// TODO remove catch nullPtr/Exc

/**
 * Algorytm:
 * 1. Z widoku 1 pobieramy odnosniki do list widoku 2 i linki bezpośrednie.
 * 2. Z widoku 2 linki bezposrednie (jest paginacja).
 */
public class IdealoSelector extends DELabProductSelector {

	private boolean view1Processed = false;
	private String targetUrlPrefix = null;

	public IdealoSelector(String country, String source, String targetUrlPrefix) throws ConnectionException {
		super(country, source);
		this.targetUrlPrefix = targetUrlPrefix;
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		view1Processed = false; //HACK
		String encoded = getSourceURL() + targetUrlPrefix + ".html?q=" +
				Utils.urlEncodeSpecial(product, '~', '"', '<', '>');
		return Utils.stringToURL(encoded);
	}

	@Override
	public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());
		final List<URL> urls = new LinkedList<>();

		if (!view1Processed) {
			//logika dla widoku 1 (wystepuja linki do produktow bezposrednio i do list produktow, chcemy tylko
			//jedna strone unikając zagłębień (sytacje widok 1 -> widok 1 -> widok 2), nie tracimy w ten sposob rekordow
			Elements elements = document.select("a.link-composed-2");
			if (elements.isEmpty()) {
				elements = document.select("td.tile.product-tile");
			}
			for (Element element : elements) {
				try {
					final String href = element.attr("abs:href");
					urls.add(new URL(href));
				} catch (MalformedURLException e) {
					logger.warn(e.getMessage());
				}
			}

			if (elements.size() > 0) {
				view1Processed = true;
			}
		} else {
			//logika dla widoku 2, moga wystapic kolejne strony
			try {
				final Elements select = document.select("a.page-next.webtrekk");
				if (!select.isEmpty()) {
					urls.add(new URL(select.first().attr("abs:href")));
				}
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}

		return urls;
	}

	@Override
	public Object getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();

		if (!document.select("div.no-result-info").isEmpty()) {
			return products;
		}

		//logika dla pobierania dla widoku 1 (linki bezposrednie zamiast linkow do widoku 2)
		Elements elements = document.select("td.tile.offer-tile");

		Date date = new Date();
		for (Element element : elements) {
			final ProductResult product = buildProductResultDirectLink(element, date);
			products.add(product);
		}

		//logika dla pobierania dla widoku 2
		elements = document.select("table.list.modular tr[data-offer-id]");
		if (elements.isEmpty()) {
			elements = document.select("table.list.modular tbody tr");
		}
		for (Element element : elements) {
			if (element.children().size() == 5) { //eliminating first row
				ProductResult product = buildProductResult(element, date);
				products.add(product);
			}
		}

		return products;
	}

	private ProductResult buildProductResult(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURL(element);
		product.setCountry(getCountry());
		product.setPrice(getPrice(element));
		product.setSearcher(getSourceURL().toString());
		if (shopURL != null) {
			product.setShopURL(shopURL.toString());
		}
		product.setShop(getShopName(element));
		product.setProduct(getProductName(element, product.getShop()));
		product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());

		return product;
	}

	private String getShopName(Element element) {
		String res = UNKNOWN;
		Elements select = element.select("td.rating img[src]");
		if (!select.isEmpty()) {
			res = select.first().attr("alt");
		}
		return res;
	}

	private Object getPrice(Element element) {
		return element.select("span.price").text();
	}

	private URL getShopURL(Element element) {
		URL res = null;
		final Elements select = element.select("td.cta > a[href]");
		if (!select.isEmpty()) {
			res = followUrl(select.first().attr("abs:href"));
		}
		return res;
	}

	private ProductResult buildProductResultDirectLink(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURLDirectLink(element);
		product.setCountry(getCountry());
		product.setPrice(getPriceDirectLink(element));
		product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(getShopNameDirectLink(element));
		product.setProduct(getProductName(element, product.getShop()));
		product.setTime(date.getTime());
		product.setProxy(getLastUsedProxy());

		return product;
	}

	private String getShopNameDirectLink(Element element) {
		return element.select("td.shop img[src]").attr("alt");
	}

	private Object getPriceDirectLink(Element element) {
		return element.select("span.price.link-1").text();
	}

	private String getProductName(Element element, String shop) {
		final String GET_CONTENTS = "getContents(";
		final String ENDING = "');/* ]]>";
		final String ENDING2 = "');\n/* ]]>";
		String res = UNKNOWN;
		if (shop.contains("Amazon") || shop.contains("amazon-marketplace") || shop.contains("Ebay")) {
			try {
				//magia parsowania, miliard przypadków
				final String script = element.select("a.offer-title.link-2.webtrekk.wt-prompt").first().toString();
				final String cuttedPrefix = script.substring(script.indexOf(GET_CONTENTS) + GET_CONTENTS.length() + 1);
				int indexEnd = cuttedPrefix.indexOf(ENDING);
				if (indexEnd == -1) {
					indexEnd = cuttedPrefix.indexOf(ENDING2);
				}
				if (indexEnd == -1) {
					res = element.select("a.offer-title.link-2.webtrekk.wt-prompt").first().text();
				} else {
					final String cuttedSuffix = cuttedPrefix.substring(0, indexEnd);
					res = Utils.Rot47PasswordEncoder.encodePassword(cuttedSuffix, "").replace("&shy;", "");
				}
			} catch (Exception e) {
				logger.error("Problem with parsing product name - amazon/ebay");
			}
		} else {
			res = element.select("a.offer-title.link-2.webtrekk.wt-prompt").text();
		}
		return res;
	}

	private URL getShopURLDirectLink(Element element) {
		return followUrl(element.select("a.offer-title.link-2.webtrekk.wt-prompt").first().attr("abs:href"));
	}
}
