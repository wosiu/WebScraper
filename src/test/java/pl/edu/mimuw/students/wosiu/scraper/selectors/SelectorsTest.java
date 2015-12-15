package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

// TODO testNG and categories
public class SelectorsTest {

	@Test
	public void testFranceIdealo() throws Exception {
		String html = "/Microsoft Manette filaire Xbox One pour Windows (noir) Manette PC _ prix à comparer sur " +
				"idealo.fr.html";
		Selector selector = new FranceIdealo();
		Document doc = readTestDocument(html);
		List products = (List) selector.getProducts(doc);
		assertEquals(products.size(), 9);
		productTest(products);

		List nexts = (List) selector.getNextPages(doc);
		assertEquals(nexts.size(), 0);
	}

	@Test
	public void testPolandCeneo() throws Exception {
		String html = "/Nike Free 5 0 TR Fit 4 - Ceneo.pl strona 2.html";
		Selector selector = new PolandCeneo();
		Document doc = readTestDocument(html);

		List products = (List) selector.getProducts(doc);
		assertEquals(products.size(), 0);
		productTest(products);

		//List nexts = (List) selector.getNextPages(doc);
		//assertEquals(nexts.size(), 0);
	}

	@Test
	public void testPolandCeneoShoes() throws Exception {
		String html = "/Converse Chuck Taylor All Star Moda - Ceneo.pl.html";
		DELabProductSelector selector = new PolandCeneo();
		selector.setRedirectShopLink(false);
		Document doc = readTestDocument(html);
		//System.out.println(doc.select("div.category-list-body.js_category-list-body.js_search-results a" +
		//		".grid-item__thumb").size());
		List products = (List) selector.getProducts(doc);
		assertFalse(products.isEmpty()); //TODO exact number
		productTest(products);

		//List nexts = (List) selector.getNextPages(doc);
		//assertEquals(nexts.size(), 0);
	}


	public Document readTestDocument(String testResource) throws URISyntaxException, IOException {
		File file = new File(this.getClass().getResource(testResource).toURI());
		//Scanner scanner = new Scanner();
		String data = Files.toString(file, Charsets.UTF_8);
		return Jsoup.parse(data);
	}

	public void productTest(List <ProductResult> products) {
		for (ProductResult product : products) {
			assertFalse(product.getProduct().isEmpty());
			assertFalse(product.getPrice().isEmpty());
			assertFalse(product.getShopURL().isEmpty());
		}
	}


	class TestCase {
		Selector selector;
		String href;
		Boolean productsIsEmpty = null;
		Boolean pagesIsEmpty = null;

		TestCase(Selector selector, String href, Boolean productsIsEmpty, Boolean pagesIsEmpty) {
			this.selector = selector;
			this.href = href;
			this.productsIsEmpty = productsIsEmpty;
			this.pagesIsEmpty = pagesIsEmpty;
		}
	}

//	@Test
	public void testOnline() throws ConnectionException {
		// log4j
		BasicConfigurator.configure();

		TestCase[] tests = {
				new TestCase(new NetherlandsBeslist(), "http://www.beslist.nl/products/r/salomon+icetown/", false, true)
		};

		for (TestCase test : tests) {
			Document document = test.selector.download(Utils.USER_AGENT, Utils.stringToURL(test.href));
			List<ProductResult> res = (List<ProductResult>) test.selector.getProducts(document);
			List pages = test.selector.getNextPages(document);
			System.out.println("Testing: '" + test.href + "'");
			assertEquals(test.productsIsEmpty, res.isEmpty());
			assertEquals(test.pagesIsEmpty, pages.isEmpty());
		}
	}

	// TODO group: online
	//@Test
	public void testPriceRunner() throws ConnectionException {
		// log4j
		//BasicConfigurator.configure();

		Selector selector;
		Document document;
		List<ProductResult> res;
		List pages;
		String url;

		// UK
		selector = new UnitedKingdomPricerunner();

		url = "http://www.pricerunner.co.uk/cl/52/Game-Consoles#q=xbox+one+500gb&search=xbox+one+500gb";
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertTrue(res.isEmpty());
		assertFalse(pages.isEmpty());
		assertEquals(pages.size(), 3); //can change a bit as search result might change

		url = "http://www.pricerunner.co.uk/pli/52-2990700/Game-Consoles/Microsoft-Xbox-One-500GB-Compare-Prices";
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertFalse(res.isEmpty());
		assertTrue(pages.isEmpty());

		url = selector.prepareTargetUrl("nike air force").toString();
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertFalse(res.isEmpty());
		assertTrue(pages.isEmpty());

		// Sweden
		selector = new SwedenPricerunner();

		url = selector.prepareTargetUrl("xbox one 500gb").toString();
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertTrue(res.isEmpty());
		assertFalse(pages.isEmpty());
		assertEquals(pages.size(), 3); //can change a bit as search result might change

		url = "http://www.pricerunner.se/pl/52-2990700/Spelkonsoler/Microsoft-Xbox-One-500GB-priser";
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertFalse(res.isEmpty());
		assertTrue(pages.isEmpty());

		url = selector.prepareTargetUrl("Nike Air Force").toString();
		document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		res = (List<ProductResult>) selector.getProducts(document);
		pages = selector.getNextPages(document);
		assertFalse(res.isEmpty());
		assertTrue(pages.isEmpty());
	}

	@Test
	public void testTEMP() throws ConnectionException {

		Selector selector;
		Document document;
		List<ProductResult> res;
		List pages;
		String url;

	}


}
