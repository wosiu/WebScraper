package pl.edu.mimuw.students.wosiu.scraper.delab.selectors;

import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class GermanPreisvergleich extends Selector {

	public GermanPreisvergleich() throws MalformedURLException, URISyntaxException {
		super();
		setCountry("Germany");
		setSource("preisvergleich.de");
		addProxy("46.101.167.103", 8110);
	}

	//todo
	public URL prepareTargetUrl(String product) {
		return null;
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
