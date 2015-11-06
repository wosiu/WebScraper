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
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) orignal.openConnection();
			con.setInstanceFollowRedirects(true);
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.connect();
			InputStream is = con.getInputStream();
			is.close();
		} catch (IOException e) {
			//nie wazne dla nas (http 403)
		}
		if (con != null) {
			redirectedUrl = con.getURL();
			con.disconnect();
		}

		return redirectedUrl;
	}

	/*public static URL redirect2(String url) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) (new URL(url).openConnection());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int responseCode = 0;
		try {
			responseCode = conn.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (responseCode == 301) {
			String location = conn.getHeaderField("Location");
			try {
				conn = (HttpURLConnection) (new URL(location).openConnection());
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.setInstanceFollowRedirects(false);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			try {
				conn.connect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return conn.getURL();
	}*/
}
