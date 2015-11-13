package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.selectors.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;


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




	public static void main(String[] args) throws IOException, URISyntaxException, ConnectionException {
		BasicConfigurator.configure();
		String url =
				//"http://www.buscape.com.br/xbox+one";
				//"http://www.bestprice.gr/search?refqid=34D6eM2uR3I_839ed&q=xbox+one";
				//"http://www.bestprice.gr/search?q=Fujifilm+X+T10+body";
				//"http://www.bestprice.gr/search?q=xbox+one";
				"http://www.bestprice.gr/item/2153473554/microsoft-xbox-one.html?qid=34D7PjT7DDh_178b0&seq=1";
		Selector selector = new GreeceBestPrice();
		Document document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));

		System.out.println(selector.getNextPages(document));
		System.out.println(selector.getProducts(document));
//		System.out.println(selector.getNextPages(document));
	}

	public static void main2(String[] args) throws IOException, URISyntaxException, ConnectionException {
		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		Selector selector = new HungaryArukereso();

		String base = "";
		base = "http://pricespy.ie/product.php?p=2661725";
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
