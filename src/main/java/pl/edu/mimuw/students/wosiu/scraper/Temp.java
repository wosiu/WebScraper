package pl.edu.mimuw.students.wosiu.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Temp {

	public static void main(String[] args) throws IOException, URISyntaxException {

		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		String base = "https://www.whatismyip.com/";
		URI uri = new URI(base);
		URL url = uri.toURL();

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"193.9.9.66", 80)); // or whatever your proxy is

		HttpURLConnection uc = (HttpURLConnection) url.openConnection(proxy);
		uc.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) " +
				"Gecko/20100316 Firefox/3.6.2");

		uc.connect();

		String line = null;
		StringBuffer tmp = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream()));

		while ((line = in.readLine()) != null) {
			tmp.append(line + "\n");
		}

		Document doc = Jsoup.parse(String.valueOf(tmp));

		uc.disconnect();
		System.out.println(doc);

	}

}
