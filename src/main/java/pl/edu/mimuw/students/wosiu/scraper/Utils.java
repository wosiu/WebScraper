package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

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

	public static URL getRedirectUrl(String url) {
		URL redirectedUrl = null;
		URLConnection con = null;
		try {
			con = new URL(url).openConnection();
			con.connect();
			InputStream is = con.getInputStream();
			is.close();
		} catch (IOException e) {
			//nie wazne dla nas (http 403)
		}
		if (con != null) {
			redirectedUrl = con.getURL();
		}

		return redirectedUrl;
	}

}
