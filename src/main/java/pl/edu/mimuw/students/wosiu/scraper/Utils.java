package pl.edu.mimuw.students.wosiu.scraper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
	public static URL stringToURL(String url) throws URISyntaxException, MalformedURLException, IllegalArgumentException {
		// create ConnectionException
		URI uri = new URI(url);
		URL targetURL = uri.toURL();
		return targetURL;
	}

}
