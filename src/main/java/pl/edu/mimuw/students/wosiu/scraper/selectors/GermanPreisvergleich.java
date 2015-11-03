package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class GermanPreisvergleich extends Selector {

	public GermanPreisvergleich() throws MalformedURLException, URISyntaxException {
		super();
		setCountry("Germany");
		setSource("http://preisvergleich.de/");
		addProxy("62.163.129.14", 80);
	}

	//todo
	public URL prepareTargetUrl(String product) throws MalformedURLException, URISyntaxException {
		URL url = Utils.stringToURL("http://www.preisvergleich.de/search/result/query/laptop/");
		return url;
	}

	@Override
	public List<Object> getProducts(Document document) {
		List<Object> asd = new LinkedList<>();
		asd.add("lorem");
		asd.add("ipsum");
		asd.add("dolor");
		asd.add("sit");

		return asd;
	}

	@Override
	public URL getNextPage(Document document) {
		return null;
	}

}
