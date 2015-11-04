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

	public static void main2(String[] args) {
		String IPADDRESS_PATTERN =
				"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
/*
		String patternString = "(.+)(PROXY_COUNTRY\":\")([a-zA-Z ]+)(\",\"PROXY_IP\":\")(.+)(\"," +
				"\"PROXY_LAST_UPDATE\":\")(.+)(PROXY_PORT\":\")([a-zA-Z ]+)(.+)(\",\"PROXY_REFS\":)(.+)";

		String str = "gp.insertPrx({\"PROXY_CITY\":\"\",\"PROXY_COUNTRY\":\"Czech Republic\",\"PROXY_IP\":\"195.113" +
				".72.12\",\"PROXY_LAST_UPDATE\":\"387 33\",\"PROXY_PORT\":\"50\",\"PROXY_REFS\":null,\"PROXY_STATE\":\"\",\"PROXY_STATUS\":\"OK\",\"PROXY_TIME\":\"51\",\"PROXY_TYPE\":\"Elite\",\"PROXY_UID\":null,\"PROXY_UPTIMELD\":\"1/0\"});\n";
*/

		String patternString =
				"(.+)" +
				"(PROXY_COUNTRY\":\")" +
				"([a-zA-Z ]+)" +
				"(\",\"PROXY_IP\":\")" +
				"(.+)" +
				"(\",\"PROXY_LAST_UPDATE\":\")" +
				"(.+)" +
				"(PROXY_PORT\":\")" +
				"([0-9a-fA-F]+)" +
				"(\",\"PROXY_REFS\":)" +
				"(.+)"
/**/				;

		String example =
				"gp.insertPrx({\"PROXY_CITY\":\"\",\"" +
				"PROXY_COUNTRY\":\"" +
				"Czech Republic" +
				"\",\"PROXY_IP\":\"" +
				"195.113.72.12" +
				"\",\"PROXY_LAST_UPDATE\":\"" +
				"387 33\",\"" +
				"PROXY_PORT\":\"" +
				"50" +
				"\",\"PROXY_REFS\":" +
				"null,\"PROXY_STATE\":\"\",\"PROXY_STATUS\":\"OK\",\"PROXY_TIME\":\"51\",\"PROXY_TYPE\":\"Elite\",\"PROXY_UID\":null,\"PROXY_UPTIMELD\":\"1/0\"});\n"
/**/				;



	}

	public static void main(String[] args) throws IOException, URISyntaxException {

		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		String base = "http://www.gatherproxy.com/proxylist/country/?c=Czech%20Republic";
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

		document.setBaseUri(base);

		//		System.out.println(doc);



		try {
			/*Elements elements = doc.getElementsByClass("next");
			Element next = elements.first().select("a").first();
			String nextStrUrl = next.attr("abs:href");
			System.out.println(nextStrUrl);*/

			/*Source htmlSource = new Source(tmp.toString());
			Segment htmlSeg = new Segment(htmlSource, 0, htmlSource.length());
			Renderer htmlRend = new Renderer(htmlSeg);
			System.out.println(htmlRend.toString());*/

			/*String html = tmp.toString(); //your external method to get html from memory, file, url etc.
			HtmlCompressor compressor = new HtmlCompressor();
			String compressedHtml = compressor.compress(html);
			System.out.println(compressedHtml);*/



		} catch (
				NullPointerException e
				)

		{
			System.out.print("dupa");

		}


	}

}
