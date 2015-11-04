package pl.edu.mimuw.students.wosiu.scraper.selectors.proxy;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gatherproxy extends Selector {

	public Gatherproxy() {
		try {
			setSource("http://www.gatherproxy.com/");
		} catch (ConnectionException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String productURL = getSourceURL() + "proxylist/country/?c=" + product;
		return Utils.stringToURL(productURL);
	}

	@Override
	public List<Object> getProducts(Document document) {
		Element table = document.select("tbody").first();
		Elements rows = table.select("tr");
		Element row = rows.first();
		Element col = row.select("td").get(1).children().first();
		Elements elements = col.children();
		return null;
	}


	@Override
	public URL getNextPage(Document document) {
		return null;
	}
}
