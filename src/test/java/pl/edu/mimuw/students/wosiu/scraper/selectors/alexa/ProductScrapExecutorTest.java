package pl.edu.mimuw.students.wosiu.scraper.selectors.alexa;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.alexa.DikiSelector;
import pl.edu.mimuw.students.wosiu.scraper.alexa.ProductScrapExecutor;
import pl.edu.mimuw.students.wosiu.scraper.alexa.TranslateExecutor;
import pl.edu.mimuw.students.wosiu.scraper.selectors.AlexaFrisco;
import pl.edu.mimuw.students.wosiu.scraper.selectors.AlexaTesco;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProductScrapExecutorTest {
	@Test
	public void testScrap() throws Exception {
		ProductScrapExecutor executor = new ProductScrapExecutor ();
		executor.scrap("ser pleśniowy");
	}

	@Test
	public void testTescoSelector() throws Exception {
		Selector selector = new AlexaTesco();
		URL url = selector.prepareTargetUrl("mleko");
		Document document = selector.download(Utils.USER_AGENT, url);
		List trans = selector.getProducts(document);
		Assert.assertTrue(trans.size() > 7);
		System.out.println(trans);
	}

	@Test
	public void testTescoSelector2() throws Exception {
		String cheese = "pur balsam";
		//cheese = Utils.urlEncode(cheese);

		Selector selector = new AlexaTesco();
		URL url = selector.prepareTargetUrl(cheese);
		Document document = selector.download(Utils.USER_AGENT, url);
		List trans = selector.getProducts(document);
		Assert.assertTrue(trans.size() > 4);
		System.out.println(trans);
	}

	@Test
	public void testTescoSelector3() throws Exception {
		String cheese = "ser pleśniowy";

		Selector selector = new AlexaTesco();
		URL url = selector.prepareTargetUrl(cheese);
		Document document = selector.download(Utils.USER_AGENT, url);
		List trans = selector.getProducts(document);
		Assert.assertTrue(trans.size() > 4);
		System.out.println(trans);
	}

	@Test
	public void testFriskoSelector() throws Exception {
		Selector selector = new AlexaFrisco();
		URL url = selector.prepareTargetUrl("ser pleśniowy");
		Document document = selector.download(Utils.USER_AGENT, url);
		List trans = selector.getProducts(document);
		Assert.assertTrue(trans.size() > 7);
		System.out.println(trans);
	}
}
