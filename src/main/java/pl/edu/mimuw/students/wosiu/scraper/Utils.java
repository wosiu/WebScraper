package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class Utils {

	public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";

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
		URL orignal = null;
		try {
			orignal = new URL(url);
		} catch (MalformedURLException e) {
			return null;
		}
		URL redirectedUrl = orignal;
		URLConnection con = null;
		try {
			con = orignal.openConnection();
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
