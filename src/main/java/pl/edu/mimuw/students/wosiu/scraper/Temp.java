package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;
import pl.edu.mimuw.students.wosiu.scraper.selectors.*;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Proxygaz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.Proxy;
import java.util.*;


public class Temp {
	private static final List<Character> SPECIAL = Arrays.asList('"','*','>');



	public static void main2(String[] args) throws IOException, URISyntaxException, ConnectionException {
		System.out.println(Utils.urlEncode("Saint-Émilion Grand Cru 2009 !@#$%^&*() +-=`~;:'\"<,>./?|\\ ąśćłóœ"));
//		WebDriver driver = new FirefoxDriver();
//		driver.get("http://www.csv.lv/search?q=xbox+one");
//		System.out.println(driver.getPageSource());
//		driver.quit();
//		FirefoxDriver driver = new FirefoxDriver();
//		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//		driver.navigate().to("http://testing-ground.scraping.pro/captcha");

	}

	public static void main(String[] args) throws IOException, URISyntaxException, ConnectionException {
		BasicConfigurator.configure();
		String url =
				"http://www.beslist.nl/accessoires/d0021157460/Fujifilm_MHG-XT10_Handgreep_voor_X-T10.html";
		Selector selector = new NetherlandsBeslist();
		Document document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));

		List<ProductResult> res = (List<ProductResult>) selector.getProducts(document);
		List pages = selector.getNextPages(document);
		System.out.println("pages: " + pages.size());
		System.out.println(pages.toString().replaceAll(", ", "\n"));
		System.out.println("results: " + res.size());
		System.out.println(res.toString().replaceAll("}, ", "}\n"));
//		System.out.println(selector.getNextPages(document));
	}

	public static void main1(String[] args) throws IOException, URISyntaxException, ConnectionException {
		///Jsoup.connect(uri).userAgent(userAgent).get()
		//Jsoup.connect(uri).get()

		Selector selector = new HungaryArukereso();

		String base = "";
		base = "http://herne-konzoly.heureka.sk/microsoft-xbox-one-500gb-without-kinect?expand=1";
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
		System.out.println(document);
		uc.disconnect();
	}

}
