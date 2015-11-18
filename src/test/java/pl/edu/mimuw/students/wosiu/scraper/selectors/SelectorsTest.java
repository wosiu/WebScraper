package pl.edu.mimuw.students.wosiu.scraper.selectors;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SelectorsTest {
	@Test
	public void testDecode() throws Exception {
		String html = "/Microsoft Manette filaire Xbox One pour Windows (noir) Manette PC _ prix à comparer sur " +
				"idealo.fr.html";
		File file = new File(this.getClass().getResource(html).toURI());
		//Scanner scanner = new Scanner();
		String data = Files.toString(file, Charsets.UTF_8);
		Document doc = Jsoup.parse(data);
		Selector selector = new FranceIdealo();
		List products = (List) selector.getProducts(doc);
		assertEquals(products.size(), 9);
		productTest(products);

		List nexts = (List) selector.getNextPages(doc);
		assertEquals(nexts.size(), 0);
	}

	public void productTest(List <ProductResult> products) {
		for (ProductResult product : products) {
			assertFalse(product.getProduct().isEmpty());
			assertFalse(product.getPrice().isEmpty());
			assertFalse(product.getShopURL().isEmpty());
		}
	}
}
