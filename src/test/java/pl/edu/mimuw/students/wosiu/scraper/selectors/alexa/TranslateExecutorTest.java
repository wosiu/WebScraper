package pl.edu.mimuw.students.wosiu.scraper.selectors.alexa;

import org.jsoup.nodes.Document;
import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.alexa.DikiSelector;
import pl.edu.mimuw.students.wosiu.scraper.alexa.TranslateExecutor;

import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

public class TranslateExecutorTest {

	@Test
	public void testTranslate() throws Exception {
		TranslateExecutor translator = new TranslateExecutor();
		assertEquals("mleko", translator.translate("milk"));
		assertEquals("piwo", translator.translate("beer"));
	}

	@Test
	public void testDikiSelector() throws Exception {
		Selector selector = new DikiSelector();
		URL url = selector.prepareTargetUrl("milk");
		Document document = selector.download(Utils.USER_AGENT, url);
		List<String> trans = selector.getProducts(document);
		assertEquals("mleko", trans.get(0));
	}
}
