package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
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
public class GreeceBestPrice extends DELabProductSelector {

    public GreeceBestPrice() throws ConnectionException {
        super("Greece", "http://www.bestprice.gr/");
    }

    @Override
    public URL prepareTargetUrl(String product) throws ConnectionException {
        String encoded = getSourceURL() + "products/r/" +
                Utils.urlEncode(product.toLowerCase().trim()).replaceAll(" ", "+") + "/";
        return Utils.stringToURL(encoded);
    }

    @Override
    public List<URL> getNextPages(Document document) {
        document.setBaseUri(getSourceURL().toString());
        final List<URL> urls = new LinkedList<>();

        final Elements elements = document.select("td.one-merchant");
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

        if (!document.toString().contains("Δεν βρέθηκαν αποτελέσματα, δοκίμασε " +
                "να τροποποιήσεις τους όρους αναζήτησης και προσπάθησε ξανά.")) {
            Elements elements = document.select("tbody.physical-products").select("tr");
            Date date = new Date();
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
        product.setCountry("Greece");
        product.setPrice(getPrice(element));
        product.setSearcher("BestPrice");
        product.setShopURL(shopURL.toString());
        product.setShop(getShopName(element));
        product.setProduct(getProductName(element));
        product.setTime(date.getTime());
        return product;
    }

    private String getShopName(Element element) {
        return element.select("td.store a.mbanner").attr("title");
    }

    private String getProductName(Element element) {
        return element.select("th.descr a.title.no-img").first().text();
    }

    private String getPrice(Element element) {
        return element.select("a.button.tomer.title.no-img").first().text();
    }

    private URL getShopURL(Element element) {
        URL res = null;
        try {
            res = Utils.stringToURL(element.select("th.descr a.title.no-img").first().attr("abs:href"));
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return res;
    }
}
