package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public abstract class YellowSelector extends DELabProductSelector {

    final private String targetInfix;

    public YellowSelector(String country, String source, String targetInfix) throws ConnectionException {
        super(country, source);
        this.targetInfix = targetInfix;
    }

    @Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
        // change diacritics
        product = Utils.urlEncodeSpecial(product, '~', '"', '<', '>');

        String target = getSourceURL() + targetInfix + "?q=" + product;
        URL url = Utils.stringToURL(target);
        return url;
	}

	@Override
	public List<URL> getNextPages(Document document) {
		final List<URL> urls = new LinkedList<>();
        final Elements elements = document.select("div.innerProductBox div.imgWrap a[href]");

		for (Element element : elements) {
			try {
                urls.add(new URL(element.attr("href")));
			} catch (MalformedURLException e) {
				logger.warn(e.getMessage());
			}
		}
		return urls;
	}

	@Override
	public List getProducts(Document document) {
		List<ProductResult> products = new LinkedList<>();

		Date date = new Date();
        final Elements pname = document.select("div.newPPh1 h1[itemprop]");
        String productName = null;
        if (!pname.isEmpty()) {
            productName = pname.first().text();
        } else {
            return products;
        }

        final Elements elements = document.select("div.offerByBrandB");
		for (Element element : elements) {
            products.add(buildProductResult(element, productName, date));
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
        if (shopURL != null) {
            product.setShopURL(shopURL.toString());
        }
        product.setShop(getShopName(element));
		product.setTime(date.getTime());
        product.setProxy(getLastUsedProxy());
		return product;
	}

    private String getShopName(Element element) {
        String res = "";
        final Elements select = element.select("div.topRow img[src]");
        if (!select.isEmpty()) {
            res = select.first().attr("alt");
        } else {
            logger.warn("Can't get host name");
        }
        return res;
	}

    public URL getShopURL(Element element) {
        URL res = null;
        final Elements select = element.select("div.priceBox a[href]");
        if (!select.isEmpty()) {
            res = followUrl(select.first().attr("href"));
        } else {
            logger.warn("Can't get shop url");
        }
        return res;
    }

	private String getPrice(Element element) {
        String res = "";
        final Elements select = element.select("div.price a");
        if (!select.isEmpty()) {
            res = select.text();
        } else {
            logger.warn("Can't get price");
        }
        return res;
	}

	@Override
	public Document read(HttpURLConnection connection) throws IOException {
		Document doc = super.read(connection);

		if (!doc.select("input[name=recaptcha_response_field]").isEmpty()) {
			throw new IOException("Captcha occurred");
		}

        String title = doc.select("title").first().text();
        if (title.contains("Moved Permanently")) {
            throw new IOException("Wrong view appeared, title: " + title);
        }


		return doc;
	}
}
