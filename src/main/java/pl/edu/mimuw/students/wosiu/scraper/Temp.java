package pl.edu.mimuw.students.wosiu.scraper;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.BasicConfigurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.xalan.xsltc.compiler.util.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
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
	private static final List<Character> SPECIAL = Arrays.asList('"', '*', '>');

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

//		String link = "http://www.skyscanner.pl/transport/loty/waw/lhr/151119/151120/ceny-biletow-lotniczych-z-warszawa" +
//		"-okecie-do-londyn-heathrow-w-listopad-2015.html?adults=1&children=0&infants=0&cabinclass=economy&rtn=1&preferdirects=false&outboundaltsenabled=false&inboundaltsenabled=false#results";

		String link = "http://www.lufthansa.com/online/portal/lh/pl/homepage";

		driver.get(link);
		System.out.println("Got");

//		manageCookies(driver);
		// Wyszukiwanie: div.day-searching-message
		Thread.sleep(5000);
//		System.out.println("wake up");
//		manageCookies(driver);


		System.out.println(driver.getTitle());
		System.out.println(driver.getPageSource());
		//driver.quit();
//		System.out.println(driver.getPageSource());
//		driver.quit();
//		FirefoxDriver driver = new FirefoxDriver();
//		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//		driver.navigate().to("http://testing-ground.scraping.pro/captcha");

	}

	public static void main8(String[] args) throws IOException, InterruptedException, ConnectionException {

		String content = "";
		DefaultConfiguration configuration = new DefaultConfiguration();
		configuration.initialize();
		String url = "http://www.pricerunner.co.uk/cl/52/Game-Consoles#q=xbox+one+500gb&search=xbox+one+500gb";
		url = "http://www.pricerunner.co.uk/cl/52/Game-Consoles#q=sony+playstation+4+console&search=sony+playstation" +
				"+4+console";
		//url = "http://www.pricerunner.co.uk/pli/52-2990700/Game-Consoles/Microsoft-Xbox-One-500GB-Compare-Prices";
//		HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.CHROME);
//		driver.setJavascriptEnabled(true);
//		driver.get(url);
//		System.out.println( driver.getPageSource() );

//		DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
//		capabilities.setBrowserName("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20160101 Firefox/66.0");
//		HtmlUnitDriver driver = new HtmlUnitDriver(/*capabilities*/);
//		driver.setJavascriptEnabled(true);
//
//		driver.get(url);
//		System.out.println(driver.getPageSource());

//		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
//		webClient.getOptions().setJavaScriptEnabled(true);
//		webClient.getOptions().setThrowExceptionOnScriptError(false);
//
//		Page page = webClient.getPage(url);
//
//		int i = webClient.waitForBackgroundJavaScript(2000);
//
//		WebResponse response = page.getWebResponse();
//
//		i = webClient.waitForBackgroundJavaScript(2000);
//
//		String content = response.getContentAsString();
//
//		System.out.println("tu:");
//		System.out.println(content);
//WORKS!:
/*
		WebClient webClient = new WebClient(BrowserVersion.FIREFOX_38);
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		//webClient.setAjaxController(new NicelyResynchronizingAjaxController());

		System.out.println("web client created");
		WebRequest request = new WebRequest(new URL(url));
		System.out.println("request created");

		HtmlPage page = webClient.getPage(request);
//		Document document = new Document(url);
//		HtmlPage p = new HtmlPage(document.text());
		System.out.println("start");
		//int i = webClient.waitForBackgroundJavaScript(10000);
		//System.out.println("iiiiiiiiiiiiiiiiiiiiiiiiiiiiii:" + i);
//		System.out.println(page.asXml());
/**/

		Selector selector = new UnitedKingdomPricerunner();
		Document document = selector.download(Utils.USER_AGENT, new URL(url));

		System.out.println("client2");
		StringWebResponse response = new StringWebResponse
				(document.toString(), new URL(url));
		WebClient client = new WebClient(BrowserVersion.FIREFOX_38);
		client.getOptions().setJavaScriptEnabled(true);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getPage(url);
		System.out.println("parsing");
		HtmlPage page2 = HTMLParser.parseHtml(response, client.getCurrentWindow());
		System.out.println("waiting for js");
		client.waitForBackgroundJavaScript(20000);
		System.out.println(page2.asXml());


//		while (i > 0)
//		{
//			i = webClient.waitForBackgroundJavaScript(1000);
//
//			if (i == 0)
//			{
//				break;
//			}
//			synchronized (page)
//			{
//				System.out.println("wait");
//				page.wait(500);
//			}
//		}

//		webClient.getAjaxController().processSynchron(page, request, false);
//
//		System.out.println(page.asXml());

	}

	public static void main(String[] args) throws IOException, URISyntaxException, ConnectionException,
			ParseException {
		BasicConfigurator.configure();
		Selector selector = new UnitedKingdomPricerunner();

		String url = "http://www.pricerunner.co.uk/cl/52/Game-Consoles#search=xbox+one+500gb";
		url = "http://www.pricerunner.co.uk/pli/52-2990700/Game-Consoles/Microsoft-Xbox-One-500GB-Compare-Prices";
//				"http://www.beslist.nl/accessoires/d0021157460/Fujifilm_MHG-XT10_Handgreep_voor_X-T10.html";
		//selector.addProxy("52.19.27.164", 80);

		//url = selector.prepareTargetUrl("xbox one 500gb").toString();
		System.out.println(url);
		Document document = selector.download(Utils.USER_AGENT, Utils.stringToURL(url));
		System.out.println(document);
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
//		base = "http://www.skyscanner.pl/transport/loty/lond/scq/160201/160205/ceny-biletow-lotniczych-z-londyn-do" +
//				"-santiago-de-compostela-w-luty-2016.html?adults=1&children=0&infants=0&cabinclass=economy&rtn=1&preferdirects=true&outboundaltsenabled=false&inboundaltsenabled=false#results";
		base = "http://www.google.pl/flights/#search;f=SJC;t=EWR,JFK,LGA;d=2015-12-24;r=2015-12-31";
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
