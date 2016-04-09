package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionWrapper;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * This abstract of selector is not sensitive for setting user agent (uses only firefox 3.8)
 * For downloading page uses different strategy than is implemented in subclass - WebClient from htmlunit library.
 * We need to wait for javascript being done as results are filtered on the client side.
 *
 */

public abstract class PricerunnerSelector extends DELabProductSelector {

	private WebClient webClient = null;
	private String shopNamePrefix = "";
	public static final int JAVA_SCRIPT_WAIT_MS = 3000;

    public PricerunnerSelector(String country, String source, String shopNamePrefix) throws ConnectionException {
        super(country, source);

		this.shopNamePrefix = shopNamePrefix;

		// if want to set user agent in future change below default browser to something more fancy
		this.webClient = new WebClient(BrowserVersion.FIREFOX_38);
		this.webClient.getOptions().setJavaScriptEnabled(true);
		this.webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

    @Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
        // change diacritics
        product = Utils.urlEncodeSpecial(product);

        String target = getSourceURL() + "search" + "?q=" + product;
        // need to redirect once
		URL redirected = Utils.getRedirectUrl(target);
        return redirected;
	}

	@Override
	public Document download(URL targetURL, ConnectionWrapper cw) throws ConnectionException {
		Document document;
		// product view - need to run javascript to filter the results
		if (targetURL.toString().contains("/cl/")) {
			document = downloadViaHtmlUnit(targetURL, cw);
		} else {
			// for offert view we don't need to run javascript - use default download method
			document = super.download(targetURL, cw);
		}

		if (document == null || document.title().isEmpty() || document.title().contains("Site Unavailable") ||
				document.title().contains("McAfee Web Gateway") ) {
			return null;
		}
		return document;
	}

	public Document downloadViaHtmlUnit(URL targetURL, ConnectionWrapper cw) throws ConnectionException {
		final String connectionInfoMsg = "url: " + targetURL.toString() + ", " + cw.toString();

//		int readTimeout = cw.getReadTimeoutMs();
		WebRequest request = new WebRequest(targetURL);
		Proxy proxy = cw.getProxy();

		if (proxy != null) {
			InetSocketAddress address = (InetSocketAddress) proxy.address();
//			ProxyConfig proxyConfig = new ProxyConfig(address.getHostString(), address.getPort());
//			webClient.getOptions().setProxyConfig(proxyConfig); // TODO set no proxy as can change to local
			request.setProxyHost(address.getHostString());
			request.setProxyPort(address.getPort());
			logger.info("User agent not used in request. Used default one for this selector.");
			this.webClient.getOptions().setTimeout(CONNECTION_TIMEOUT_MS + READ_TIMEOUT_MS);
		} else {
			this.webClient.getOptions().setTimeout(LOCAL_CONNECTION_TIMEOUT_MS + LOCAL_READ_TIMEOUT_MS);
		}

		try {
			do {
				try {
					long start = System.currentTimeMillis();

					HtmlPage page = webClient.getPage(request);
					int i = webClient.waitForBackgroundJavaScript(JAVA_SCRIPT_WAIT_MS);

					long elapsed = (System.currentTimeMillis() - start) / 1000;
					logger.debug("Connected and read in: " + elapsed + "s to: " + targetURL + ", " + cw);

					Document doc = Jsoup.parse(page.asXml());

					if (getSourceURL() != null) {
						doc.setBaseUri(getSourceURL().toString());
					}

					return doc;

				} catch ( UnknownHostException | SocketException e) {
					logger.info(connectionInfoMsg);
					logger.info(e.toString());
					logger.info("Reconnecting...");
					continue;
				} catch (SocketTimeoutException | java.io.FileNotFoundException | FailingHttpStatusCodeException e) {
					logger.info(connectionInfoMsg);
					logger.info(e.toString());
					break;
				} catch (IOException e) {
					logger.warn(connectionInfoMsg);
					logger.warn(e.toString());
					break;
				}
			} while (waitForNetwork());

		} catch (InterruptedException e) {
			logger.error("Cannot sleep thread while waiting for reconnect");
		}

		return null;
	}

	@Override
    public List<ProductResult> getProducts(Document document) {
        if (!document.select("selection.noexactmatch").isEmpty()) {
            logger.debug("Empty search page occurred");
            return Arrays.asList();
        }
        List<ProductResult> results = new ArrayList<>();

		// offert view - .../pli/... for uk, .../pl/ for sweden
        for (Element element : document.select("table.price-list > tbody > tr:has(td.price):not(.product-gray)")) {
            ProductResult result = new ProductResult();

            String price = element.select("td.price").first().text();
            result.setPrice(price);

            String shopname = element.select("td.logo a.retailer-info-link").text();
            if (shopname.startsWith(shopNamePrefix)) {
				shopname = shopname.substring(shopNamePrefix.length());
			}
			result.setShop(shopname);
			Element a = element.select("td.about-retailer > h4.p-name > a[href]").first();

			String prod = a.text();
            result.setProduct(prod);

            String link = a.attr("abs:href");

            result.setShopURL(followUrl(link).toString());

            result.setCountry(getCountry());
            result.setProxy(getLastUsedProxy());
            result.setSearcher(getSourceURL().toString());

            results.add(result);
        }

		// product view .../cl/...
		// http://www.pricerunner.co.uk/cl/1430/Xbox-One-Games#q=call+of+duty+black+ops+iii+%28xbox+one%29&search=call+of+duty+black+ops+iii+%28xbox+one%29
		for (Element element : document.select("div.product-wrapper:not(:has(div.see-all-merchant)):has(p.price)")) {
			ProductResult result = new ProductResult();

			Element a = element.select("p.price > a[href][retailer-data]").first();
			String price = a.text();
			result.setPrice(price);

			String shopname = a.attr("retailer-data");
			if (shopname != null) {
				shopname = shopname.replaceAll("\\(.+\\)", "");
			}
			result.setShop(shopname);

			String prod = element.select("h3").text();
			result.setProduct(prod);

			String link = a.attr("abs:href");
			result.setShopURL(followUrl(link).toString());

			result.setCountry(getCountry());
			result.setProxy(getLastUsedProxy());
			result.setSearcher(getSourceURL().toString());

			results.add(result);
		}

        return results;
    }

    /**
     * Do not paginate. Collect links from first page if product name is too general.
     *
     * @param document
     * @return
     */
    @Override
    public List<URL> getNextPages(Document document) {
        List <URL> urls = new ArrayList<>();
        document.setBaseUri(getSourceURL().toString());
        for ( Element element : document.select("a.retailers[href]") ) {
            String str = element.attr("abs:href");
            try {
                urls.add(Utils.stringToURL(str));
            } catch (ConnectionException e) {}
        }

		// http://www.pricerunner.co.uk/cl/1430/Xbox-One-Games#q=call+of+duty+black+ops+iii+%28xbox+one%29&search=call+of+duty+black+ops+iii+%28xbox+one%29
		for ( Element element : document.select("div.product-wrapper div.see-all-merchant > a[href]") ) {
			String str = element.attr("abs:href");
			try {
				urls.add(Utils.stringToURL(str));
			} catch (ConnectionException e) {}
		}

        return urls;
    }

}
