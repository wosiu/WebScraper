package pl.edu.mimuw.students.wosiu.scraper;

import com.opencsv.CSVReader;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class BrowserOpener {
	public static Logger logger = Logger.getLogger("BrowserOpener");

	public static void main(String[] args) throws IOException, URISyntaxException, ConnectionException {

		// log4j init
		BasicConfigurator.configure();

		CSVReader reader = new CSVReader(new FileReader("empty.csv"), '\t');

		String [] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			// nextLine[] is an array of values from the line
			String url = nextLine[3];
			logger.info("Open: " + url);
			WebDriver driver = new FirefoxDriver();
			driver.get(url);
		}


//		System.out.println(driver.getPageSource());
//		driver.quit();
//		FirefoxDriver driver = new FirefoxDriver();
//		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//		driver.navigate().to("http://testing-ground.scraping.pro/captcha");
	}

}
