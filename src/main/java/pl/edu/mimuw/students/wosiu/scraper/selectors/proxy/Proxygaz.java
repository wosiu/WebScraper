package pl.edu.mimuw.students.wosiu.scraper.selectors.proxy;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyWrapper;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Proxygaz extends Selector {

	private String country;
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
		return (country == null) ? country : country.toLowerCase().replaceAll(" ", "-");
	}

	public Proxygaz(String country) {
		super();
		this.country = country;

		country = prepareCountryName(country);
		try {
			setSource("http://proxygaz.com/country/" + country + "-proxy/");
		} catch (ConnectionException e) {
			logger.error(e.toString());
		}
	}

	@Override
	public URL prepareTargetUrl(String product) throws ConnectionException {
		product = prepareCountryName(product);
		String productURL = getSourceURL() + "country/" + product + "-proxy/";
		return Utils.stringToURL(productURL);
	}

	private static final int SCRIPT_PREF_SIZE = "document.write(Base64.decode(\"".length();
	private static final int SCRIPT_SUF_SIZE = "\"))".length();

	@Override
	public List<ProxyWrapper> getProducts(Document document) {

		List<ProxyWrapper> res = new ArrayList<>();

		for (Element element : document.select("tbody > tr.plbc_bloc_proxy_tr")) {
			ProxyWrapper pw = new ProxyWrapper();

			pw.setCountry(this.country);
			String coded = element.select("td.plbc_bloc_proxy_td_ip > script").first().dataNodes().get(0).getWholeData();
			coded = coded.substring(SCRIPT_PREF_SIZE, coded.length() - SCRIPT_SUF_SIZE);
			String ip = new String(Base64.getDecoder().decode(coded));
			String portstr = element.select("td.plbc_bloc_proxy_td_port").first().text();

			int port = Integer.parseInt(portstr);

			pw.setHTTPProxy(ip, port);

			res.add(pw);
		}

		return res;
	}


	@Override
	public List<URL> getNextPages(Document document) {
		return null;
	}
}
