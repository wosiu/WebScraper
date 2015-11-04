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

public class Hidemyass extends Selector {

	public Hidemyass() {
		try {
			setSource("http://proxylist.hidemyass.com/");
		} catch (ConnectionException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		return getSourceURL();
	}

	@Override
	public List<Object> getProducts(Document document) {
		Element table = document.select("tbody").first();
		Elements rows = table.select("tr");
		Element row = rows.first();
		Element col = row.select("td").get(1).children().first();
		Elements elements = col.children();

		Set<String> disabledStyles = new HashSet<>();
		Set<String> inlineStyles = new HashSet<>();
		Element style = elements.first();

		System.out.println(elements);

		String[] styleArr = style.toString().split("\n");
		String patternString = "(\\.)(.+)(\\{)(.+?)(\\})";
		Pattern pattern = Pattern.compile(patternString);

		for (int i = 1; i < styleArr.length - 1; i++) {
			Matcher matcher = pattern.matcher(styleArr[i]);

			if (matcher.find()) {
				if (displayCompare(matcher.group(4), "display:none")) {
					disabledStyles.add(matcher.group(2).replaceAll("\\s", ""));
				} else if (displayCompare(matcher.group(4), "display:inline")) {
					inlineStyles.add(matcher.group(2).replaceAll("\\s", ""));
				}
			}
		}

		System.out.println("None: " + disabledStyles);
		System.out.println("Inline: " + inlineStyles);


		List<String> ip = new ArrayList<>(7);
		ListIterator it = elements.listIterator(1);
		while (it.hasNext()) {
			Element e = (Element) it.next();
			System.out.println(ip);
			System.out.println("------------------");
			System.out.println(e);


			String val = e.text();

			if (val.isEmpty()) {
				continue;
			}
			if (disabledStyles.contains(e.className().replaceAll("\\s", ""))) {
				continue;
			}
			if (inlineStyles.contains(e.className().replaceAll("\\s", ""))) {
				ip.add(val);
				continue;
			}
			if (displayCompare(e.attr("style"), "display:none")) {
				continue;
			}
			if (displayCompare(e.attr("style"), "display:inline")) {
				ip.add(val);
				continue;
			}
			if ( NumberUtils.isNumber(e.className()) ) {
				ip.add(val);
				continue;
			}

			// TODO there are strings without div or span in html

		}

		if (ip.size() == 7) {
			// todo concat, add result
		}
		System.out.println(ip);

		// todo iterate through all raws
		return null;
	}

	private static boolean displayCompare(String style, String type) {
		return style.replaceAll("\\s", "").equalsIgnoreCase(type);
	}

	@Override
	public URL getNextPage(Document document) {
		String base = getSourceURL().toString();
		document.setBaseUri(base);
		String nextStrUrl = null;
		URL res;

		try {
			Elements elements = document.getElementsByClass("next");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
		} catch (NullPointerException e) {
			return null;
		}

		try {
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			logger.debug(e.toString());
			return null;
		}

		return res;
	}
}
