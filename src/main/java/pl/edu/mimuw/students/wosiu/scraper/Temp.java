package pl.edu.mimuw.students.wosiu.scraper;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.htmlparser.*;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Temp {
	private static final List<Character> SPECIAL = Arrays.asList('"','*','>');


	private static String convert(String str) {
		StringBuilder out = new StringBuilder();
		for (char c : str.toCharArray()) {
			if (SPECIAL.contains(c)) {
				out.append(c);
			} else try {
				out.append(URLEncoder.encode("" + c, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// todo logger
				out.append(c);
			}
		}
		return out.toString();
	}
	public static void main3(String[] args) throws IOException, URISyntaxException {
		String wzo = "Tefal+hb866+%2B+3-+\"*.+>+%21%40%23%24%25%5E%26%28%29%3F%2C%2F%3B%3A%27%5B%5D%7B%7D%5C";
		String out = convert("Tefal hb866 + 3- \"*. > !@#$%^&()?,/;:'[]{}\\");
		System.out.println( wzo.equals(out) );
	}

	public static void main(String[] args) throws IOException, URISyntaxException {

		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		String base = "http://www.pazaruvaj.com/CategorySearch.php?st=How+to+Train+Your+Dragon+2";
		//base = "http://www.arukereso.hu/mobiltelefon-c3277/htc/one-m9-32gb-p272441289/";
		URI uri = new URI(base);
		URL url = uri.toURL();

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"91.183.124.41", 80)); // or whatever your proxy is*/

		HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		//uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) " +
		//		"Gecko/20100316 Firefox/3.6.2");

		uc.connect();

		String line = null;
		StringBuffer tmp = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));

		while ((line = in.readLine()) != null) {
			tmp.append(line + "\n");
		}
		String str;
		Document document = Jsoup.parse(tmp.toString());
		uc.disconnect();
		document.setBaseUri("http://www.preisvergleich.de/");

		System.out.println( document.select("div.image-link-container > a[href].image").first().attr
				("abs:href") );
		/*
		String nextStrUrl = null;
		URL res;

		try {
			Elements elements = document.getElementsByClass("button-orange");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
		} catch (NullPointerException e) {
			//return null;
		}
		System.out.println(nextStrUrl);
		try {
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			//logger.debug(e.toString());
			//return null;
		}*/





	}

}
