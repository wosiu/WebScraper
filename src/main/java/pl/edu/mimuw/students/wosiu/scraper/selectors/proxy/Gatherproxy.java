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


	/*String example =
			"gp.insertPrx({\"PROXY_CITY\":\"\",\"" +
					"PROXY_COUNTRY\":\"" +
					"Czech Republic" +
					"\",\"PROXY_IP\":\"" +
					"195.113.72.12" +
					"\",\"PROXY_LAST_UPDATE\":\"" +
					"387 33\",\"" +
					"PROXY_PORT\":\"" +
					"50" +
					"\",\"PROXY_REFS\":" +
					"null,\"PROXY_STATE\":\"\",\"PROXY_STATUS\":\"OK\",\"PROXY_TIME\":\"51\",\"PROXY_TYPE\":\"Elite\",\"PROXY_UID\":null,\"PROXY_UPTIMELD\":\"1/0\"});\n"
				;*/

	private static final Pattern pattern = Pattern.compile(PATTERN_STR);

	private String prepareCountryName(String country) {
		return (country == null) ? country : country.replaceAll(" ", "%20");
	}

	public Gatherproxy(String country) {
		super();

		country = prepareCountryName(country);
		try {
			setSource("http://www.gatherproxy.com/proxylist/country/?c=" + country);
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
	}

	public Gatherproxy() {
		super();
		try {
			setSource("http://www.gatherproxy.com/");
		} catch (ConnectionException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = prepareCountryName(product);
		String productURL = getSourceURL() + "proxylist/country/?c=" + product;
		return Utils.stringToURL(productURL);
	}

	@Override
	public List<ProxyWrapper> getProducts(Document document) {

		Element table = document.select("tbody").first();
		Elements rows = table.select("script");
		List<ProxyWrapper> res = new ArrayList<>(rows.size());

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
	public List<URL> getNextPages(Document document) {
		return null;
	}
}
