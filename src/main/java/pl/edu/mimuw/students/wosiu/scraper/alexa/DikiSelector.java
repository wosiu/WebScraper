package pl.edu.mimuw.students.wosiu.scraper.alexa;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DikiSelector extends Selector {

	public DikiSelector() throws ConnectionException {
		setCountry("Poland");
		setSource("https://www.diki.pl/");
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String encoded = getSourceURL() + "slownik-angielskiego?q=" +
				Utils.urlEncodeSpecial(product, '~', '"', '<', '>', '_');
		return Utils.stringToURL(encoded);
	}

	@Override
	public List getProducts(Document document) {
		//Elements select = document.select("ol.ms > li[class^=meaning] > span.hw > a[href^=/slownik-angielskiego?q=]");
		// TODO get first, but NOUN
		Elements select = document.select("ol.ms > li[class^=meaning] > span.hw");
		List<String> products = new ArrayList<>(select.size());
		for (Element element : select) {
			String product = element.text();
			if (StringUtils.isNotBlank(product)) {
				product = product.trim().toLowerCase();
				products.add(product);
			}
		}
		return products;
	}

	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}
}
