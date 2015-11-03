package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class GermanyPreisvergleich extends Selector {

	public GermanyPreisvergleich() throws MalformedURLException, URISyntaxException {
		super();
		setCountry("Germany");
		setSource("http://preisvergleich.de/");
		addProxy("46.101.167.103", 8118);
		addProxy("37.187.253.39", 8115);
	}

	//todo
	public URL prepareTargetUrl(String product) throws MalformedURLException, URISyntaxException {
		URL url = Utils.stringToURL("http://www.preisvergleich.de/search/result/query/laptop/");
		return url;
	}

	@Override
	public List<Object> getProducts(Document document) {
		List<Object> asd = new LinkedList<>();
		asd.add(document.toString().substring(0,30));

		return asd;
	}

	@Override
	public URL getNextPage(Document document) {
		return null;
	}

	@Override
	public Document getDoc(String userAgent, URL targetURL) throws ConnectionException {
		return super.getDoc(userAgent, targetURL);
	}
}
