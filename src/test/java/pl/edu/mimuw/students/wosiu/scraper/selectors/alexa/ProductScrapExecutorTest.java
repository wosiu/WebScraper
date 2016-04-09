package pl.edu.mimuw.students.wosiu.scraper.selectors.alexa;

import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.alexa.ProductScrapExecutor;
import pl.edu.mimuw.students.wosiu.scraper.alexa.TranslateExecutor;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ProductScrapExecutorTest {
	@Test
	public void testTranslate() throws Exception {
		ProductScrapExecutor executor = new ProductScrapExecutor ();
		executor.scrap("ser pleśniowy");
	}

}
