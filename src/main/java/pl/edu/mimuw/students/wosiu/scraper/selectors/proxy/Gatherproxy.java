package pl.edu.mimuw.students.wosiu.scraper.selectors.proxy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyWrapper;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Gatherproxy extends Selector {

	private static final String IPADDRESS_PATTERN =
			"((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))";

	private static final String PATTERN_STR =
			"(.+)" +
					"(PROXY_COUNTRY\":\")" +
					"([a-zA-Z ]+)" +
					"(\",\"PROXY_IP\":\")" +
					IPADDRESS_PATTERN +
					"(\",\"PROXY_LAST_UPDATE\":\")" +
					"(.+)" +
					"(PROXY_PORT\":\")" +
					"([0-9a-fA-F]+)" +
					"(\",\"PROXY_REFS\":)" +
					"(.+)";

	private static final Pattern pattern = Pattern.compile(PATTERN_STR);


	public Gatherproxy(String country) {
		super();

		// TODO this is temporary
		try {
			setSource("http://www.gatherproxy.com/proxylist/country/?c=" + country);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}

		/*try {
			setSource("http://www.gatherproxy.com/");
		} catch (ConnectionException e) {
			logger.error(e.toString());
		}*/
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		String productURL = getSourceURL() + "proxylist/country/?c=" + product;
		return Utils.stringToURL(productURL);
	}

	@Override
	public List<Object> getProducts(Document document) {

		Element table = document.select("tbody").first();
		Elements rows = table.select("script");
		List<Object> res = new ArrayList<>(rows.size());

		ListIterator it = rows.listIterator();
		while (it.hasNext()) {
			Element e = (Element) it.next();
			String inner = e.data().trim();
			Matcher matcher = pattern.matcher(inner);

			if (matcher.find()) {
				String country = matcher.group(3);
				String ip = matcher.group(5);
				String portHex = matcher.group(9);
				int portDec = Integer.parseInt(portHex, 16);
				ProxyWrapper pw = new ProxyWrapper();
				pw.setHTTPProxy(ip, portDec);
				pw.setCountry(country);
				res.add(pw);
			} else {
				logger.warn("Cannot match proxy from: " + inner);
				continue;
			}
		}

		return res;
	}


	@Override
	public URL getNextPage(Document document) {
		return null;
	}
}
