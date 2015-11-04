package pl.edu.mimuw.students.wosiu.scraper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {
	public static URL stringToURL(String url) throws ConnectionException {
		URL targetURL = null;
		try {
			URI uri = new URI(url);
			targetURL = uri.toURL();
		} catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
			throw new ConnectionException("Incorrect url: " + url + ". \nError: " + e.toString());
		}

		return targetURL;
	}

}
