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
public class FranceIdealo extends DELabProductSelector {

	// TODO !!!
	private boolean view1Processed = false;

	public FranceIdealo() throws ConnectionException {
		super("France", "http://www.idealo.fr/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String encoded = getSourceURL() + "prechcat.html?q=" +
				Utils.urlEncode(product.toLowerCase().trim()).replaceAll(" ", "+");
		return Utils.stringToURL(encoded);
	}

	@Override
    public List<URL> getNextPages(Document document) {
		document.setBaseUri(getSourceURL().toString());
		final List<URL> urls = new LinkedList<>();

		if (!view1Processed) {
			//logika dla widoku 1 (wystepuja linki do produktow bezposrednio i do list produktow, chcemy tylko
			//jedna strone unikając zagłębień (sytacje widok 1 -> widok 1 -> widok 2), nie tracimy w ten sposob rekordow
			final Elements elements = document.select("a.link-composed-2.webtrekk");
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
				urls.add(new URL(document.select("a.page-next.webtrekk").first().attr("abs:href")));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			} catch (NullPointerException npe) {
				npe.printStackTrace(); //nie ma kolejnej strony
			}
		}

		return urls;
	}

	@Override
	public Object getProducts(Document document) {
		document.setBaseUri(getSourceURL().toString());
		List<ProductResult> products = new LinkedList<>();

		if (!document.toString().contains("Aucun résultat ne correspond à la recherche")) {
			//logika dla pobierania dla widoku 1 (linki bezposrednie zamiast linkow do widoku 2)
			Elements elements = document.select("td.tile.offer-tile");

            Date date = new Date();
            for (Element element : elements) {
                try {
					products.add(buildProductResultDirectLink(element, date));
                } catch (NullPointerException e) {
                    e.printStackTrace();
				}
			}


			//logika dla pobierania dla widoku 2
			elements = document.select("table.list.modular tr");
			for (Element element : elements) {
				try {
					products.add(buildProductResult(element, date));
				} catch (NullPointerException e) {
					e.printStackTrace();
				}
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
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setProduct(getProductName(element, product.getShop()));
		product.setTime(date.getTime());
		return product;
	}

	private Object getPrice(Element element) {
		return element.select("span.price").text();
	}

	private URL getShopURL(Element element) {
		return followUrl(element.select("td.cta > a[href]").first().attr("abs:href"));
	}

	private ProductResult buildProductResultDirectLink(Element element, Date date) {
		final ProductResult product = new ProductResult();
		URL shopURL = getShopURLDirectLink(element);
        product.setCountry(getCountry());
		product.setPrice(getPriceDirectLink(element));
        product.setSearcher(getSourceURL().toString());
		product.setShopURL(shopURL.toString());
		product.setShop(shopURL.getHost());
		product.setProduct(getProductName(element, product.getShop()));
		product.setTime(date.getTime());
		return product;
	}

	private Object getPriceDirectLink(Element element) {
		return element.select("span.price.link-1").text();
	}

	private String getProductName(Element element, String shop) {
        final String GET_CONTENTS = "getContents(";
		final String ENDING = "');/* ]]>";
		final String ENDING2 = "');\n/* ]]>";
		String res = "";
		if (shop.equals("www.amazon.fr") || shop.equals("www.ebay.fr")) {
			try {
				//magia parsowania, miliard przypadków
				final String script = element.select("a.offer-title.link-2.webtrekk.wt-prompt").first().toString();
				final String cuttedPrefix = script.substring(script.indexOf(GET_CONTENTS) + GET_CONTENTS.length() + 1);
				int indexEnd = cuttedPrefix.indexOf(ENDING);
				if (indexEnd == -1) {
					indexEnd = cuttedPrefix.indexOf(ENDING2);
				}
				if (indexEnd == -1) {
					res = element.select("a.offer-title.link-2.webtrekk.wt-prompt").text();
				} else {
					final String cuttedSuffix = cuttedPrefix.substring(0, indexEnd);
					res = Utils.Rot47PasswordEncoder.encodePassword(cuttedSuffix, "").replace("&shy;", "");
				}
			} catch (Exception e) {
				logger.error("Problem przy parsowaniu nazwy produktu z amazon/ebay (francja)");
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
