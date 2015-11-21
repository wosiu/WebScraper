package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.BasicConfigurator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;
import pl.edu.mimuw.students.wosiu.scraper.selectors.*;
import pl.edu.mimuw.students.wosiu.scraper.selectors.proxy.Proxygaz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.Proxy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class Temp {
	private static final List<Character> SPECIAL = Arrays.asList('"','*','>');

	static void manageCookies(WebDriver driver) {
		WebDriver.Options options = driver.manage();
		Set<Cookie> cookies = options.getCookies();
		System.out.println(cookies.toString());
		options.deleteAllCookies();
	}


	public static void main2(String[] args) throws IOException, URISyntaxException, ConnectionException,
			InterruptedException {
		/*System.getProperties().put("http.proxySet", "true");
		System.getProperties().put("http.proxyHost", "111.11.184.51");
		System.getProperties().put("http.proxyPort", "9999");
		System.setProperty("webdriver.chrome.driver", "/usr/bin/google-chrome");*/

		WebDriver driver = new FirefoxDriver();
		//manageCookies(driver);


		// tak dlugo jak jest paseczek wyszukiwania, nie pobieraj tresci

String link = "http://www.skyscanner.pl/transport/loty/waw/lhr/151119/151120/ceny-biletow-lotniczych-z-warszawa" +
		"-okecie-do-londyn-heathrow-w-listopad-2015.html?adults=1&children=0&infants=0&cabinclass=economy&rtn=1&preferdirects=false&outboundaltsenabled=false&inboundaltsenabled=false#results";

		driver.get(link);
		System.out.println("Got");

		manageCookies(driver);
		// Wyszukiwanie: div.day-searching-message
		Thread.sleep(20000);
		System.out.println("wake up");
		manageCookies(driver);


		System.out.println(driver.getTitle());

		//driver.quit();
//		System.out.println(driver.getPageSource());
//		driver.quit();
//		FirefoxDriver driver = new FirefoxDriver();
//		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//		driver.navigate().to("http://testing-ground.scraping.pro/captcha");

	}

	public static void main(String[] args) throws IOException, URISyntaxException, ConnectionException, ParseException {
		BasicConfigurator.configure();
		String url = "http://www.hledejceny.cz/?s=xbox+one";
//				"http://www.beslist.nl/accessoires/d0021157460/Fujifilm_MHG-XT10_Handgreep_voor_X-T10.html";
		Selector selector = new CzechHledejCeny();
		selector.addProxy("217.169.190.9", 80);

		Document document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));

//		System.out.println(document);

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
		base = "http://www.skyscanner.pl/transport/loty/lond/scq/160201/160205/ceny-biletow-lotniczych-z-londyn-do" +
				"-santiago-de-compostela-w-luty-2016.html?adults=1&children=0&infants=0&cabinclass=economy&rtn=1&preferdirects=true&outboundaltsenabled=false&inboundaltsenabled=false#results";
				//"http://herne-konzoly.heureka.sk/microsoft-xbox-one-500gb-without-kinect?expand=1";
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
