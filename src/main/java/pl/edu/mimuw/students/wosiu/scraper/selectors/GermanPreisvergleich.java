package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.Selector;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class GermanPreisvergleich extends Selector {

	public GermanPreisvergleich() throws MalformedURLException, URISyntaxException {
		super();
		setCountry("German");
		setSource("preisvergleich.de");
		addProxy("46.101.167.103", 8110);
	}
}
