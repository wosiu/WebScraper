package pl.edu.mimuw.students.wosiu.scraper.alexa;

import org.apache.log4j.Logger;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProductResult;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.selectors.AlexaFrisco;
import pl.edu.mimuw.students.wosiu.scraper.selectors.AlexaTesco;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ProductScrapExecutor {

	private static final Logger logger = Logger.getLogger(ProductScrapExecutor.class);
	private static List<Selector> selectors= null;

	public ProductScrapExecutor () throws ConnectionException {
		selectors = Arrays.asList(new AlexaTesco(), new AlexaFrisco());
	}

	public void setSelectors(List<Selector> newSelectorsList) {
		this.selectors = newSelectorsList;
	}

	public List<ProductResult> scrap(String productName) {
		List<ProductResult> offers = new ArrayList<>();

		String product = Utils.fixSpaces(productName);
		for (Selector selector : selectors) {
			URL startUrl = null;
			try {
				startUrl = selector.prepareTargetUrl(product);
			} catch (ConnectionException e) {
				logger.error("Cannot process product - url malformed: " + product + ", selector: " + selector
						.getClass().getSimpleName());
				logger.error(e.toString());
				continue;
			}
			// traverse through pagination
			List results = null;
			results = selector.traverseAndCollectProducts(Utils.USER_AGENT, startUrl);
			if (results == null || results.isEmpty()) {
				logger.info("No results for: '" + product + "' with selector: " +
						selector.getClass().getSimpleName() + ", start url: " + startUrl);
			} else {
				for (Object o : results) {
					ProductResult result = (ProductResult) o;
					offers.add(result);
				}
				logger.debug(results.size() + " for: " + product + "' with selector: " +
						selector.getClass().getSimpleName() + ", start url: " + startUrl);
			}
		}
		return offers;
	}

	// mock for filling offer list with product name
	public void mockFill(String productName, List<ProductResult> offers) {
		ProductResult productResult = new ProductResult();
		productResult.setProduct(productName);
		productResult.setPrice(13.31);
		productResult.setShop("tesco");
		productResult.setShopURL("http://ezakupy.tesco.pl/pl-PL/ProductDetail/ProductDetail/2003120112699");
		offers.add(productResult);
	}

}
