package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.delab.DELabProductSelector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
		System.out.println(products.size());
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
}
