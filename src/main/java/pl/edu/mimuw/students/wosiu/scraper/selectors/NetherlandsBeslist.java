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

/**
 * Algorytm:
 * 1. Z widoku 1 pobieram odnośniki do widoku 2.
 * 2. Z widoku 2 linki do produktów.
 */
public class NetherlandsBeslist extends DELabProductSelector {

    public NetherlandsBeslist() throws ConnectionException {
        super("Netherlands", "http://www.beslist.nl/");
    }

    @Override
    public URL prepareTargetUrl(String product) throws ConnectionException {
        String encoded = getSourceURL() + "products/r/" +
                Utils.urlStripEncode(product.toLowerCase().trim()).replaceAll(" ", "+") + "/";
        return Utils.stringToURL(encoded);
    }

    @Override
    public List<URL> getNextPages(Document document) {
        document.setBaseUri(getSourceURL().toString());
        final List<URL> urls = new LinkedList<>();

        final Elements elements = document.select("div[data-catid]");
        for (Element element : elements) {
            try {
                final String href = element.select("a[href]").first().attr("abs:href");
                urls.add(new URL(href));
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

        if (!document.toString().contains("We hebben overal gezocht maar we kunnen")) {
            Elements elements = document.select("div.shoplist__block.showmyass");
            String title = "";
            try {
                title = document.select("h1.detailpage__producttitle").first().text();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            Date date = new Date();
            for (Element element : elements) {
                try {
                    products.add(buildProductResult(element, title, date));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        return products;
    }

    private ProductResult buildProductResult(Element element, String title, Date date) {
        final ProductResult product = new ProductResult();
        URL shopURL = getShopURL(element);
        product.setCountry(getCountry());
        product.setPrice(getPrice(element));
        product.setSearcher(getSourceURL().toString());
        if (shopURL != null) {
            product.setShopURL(shopURL.toString());
        }
        product.setShop(getShopName(element));
        product.setProduct(title);
        product.setTime(date.getTime());
        return product;
    }

    private String getShopName(Element element) {
        return element.select("span[data-wt-shop]").attr("data-wt-shop");
    }

    private String getPrice(Element element) {
        return element.select("input[data-price]").attr("data-price");
    }

    private URL getShopURL(Element element) {
        return null;
    }
}
