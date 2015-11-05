package pl.edu.mimuw.students.wosiu.scraper;

import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.htmlparser.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Temp {

	public static void main(String[] args) throws IOException, URISyntaxException {

		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		String base = "http://www.preisvergleich.de/produkt/Microsoft-Xbox-One-500GB/33405853-8541/";
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

		Document document = Jsoup.parse(tmp.toString());
		uc.disconnect();
		document.setBaseUri("http://www.preisvergleich.de/");

		String nextStrUrl = null;
		URL res;

		try {
			Elements elements = document.getElementsByClass("next");
			Element next = elements.first().select("a").first();
			nextStrUrl = next.attr("abs:href");
		} catch (NullPointerException e) {
			//return null;
		}

		try {
			res = Utils.stringToURL(nextStrUrl);
		} catch (ConnectionException e) {
			//logger.debug(e.toString());
			//return null;
		}





	}

}
