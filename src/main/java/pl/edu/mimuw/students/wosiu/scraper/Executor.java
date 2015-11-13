package pl.edu.mimuw.students.wosiu.scraper;

import com.opencsv.CSVWriter;
import org.apache.log4j.*;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.List;

public class Executor {

	private static final Logger logger = Logger.getLogger(Executor.class);
	private static final String LOG_LEVEL = "debug";

	public Executor() {
		log4jInit();
	}

	public static void main(String[] args) {
		Executor exe = new Executor();
		if (args.length != 1) {
			logger.error("Argument missing - add config file path.");
			return;
		}
		exe.run(args[0]);
	}

	public void log4jInit() {
		BasicConfigurator.configure();
/*
		// Turn off debugs from spring, etc..
		Level levelRoot = Level.toLevel("warn");
		Logger.getRootLogger().setLevel(levelRoot);

		ConsoleAppender appender = new ConsoleAppender();
		appender.setLayout(new PatternLayout("%d | %p %c: %m%n"));
		appender.activateOptions();
		Logger.getRootLogger().addAppender(appender);

		// Change above only for our classes
		Level levelServer = Level.toLevel(LOG_LEVEL);
		Logger.getLogger("pl.edu.mimuw.students.wosiu").setLevel(levelServer);
*/
	}

	public void run(String JSONconfigPath) {
		long start = System.currentTimeMillis();

		Config conf;
		try {
			conf = new Config(JSONconfigPath);
		} catch (ConfigException e) {
			logger.error(e.toString());
			return;
		}

		CSVWriter writer = null;
		int recordCounter = 0;

		try {
			writer = new CSVWriter(new FileWriter(conf.getOutputPath()), '\t');
		} catch (IOException e) {
			logger.error(e.toString());
			return;
		}

		// TODO moge do DELab executor
		// TODO enum with ProductResult attr and then build CSV respecting header with enum values
		String[] CSVheader = {"Product query", "Product name", "Country", "Search engine", "Price", "Shop name",
				"Proxy", "Product shop URL", "Search engine result URL", "User agent"};
		writer.writeNext(CSVheader);

		logger.info("Scraping for " + conf.getSelectors().size() + " selectors.");
		for (String userAgent : conf.getUserAgents()) {
			for (String product : conf.getProducts()) {
				for (Selector selector : conf.getSelectors()) {
					URL startUrl = null;
					try {
						startUrl = selector.prepareTargetUrl(product);
					} catch (ConnectionException e) {
						logger.error("Cannot process product - url malformed: " + product + ", selector: " + selector
								.getClass().getSimpleName());
						logger.debug(e.toString());
					}
					// traverse through pagination
					List<Object> results = null;
					results = selector.traverseAndCollectProducts(userAgent, startUrl);
					if (results.isEmpty()) {
						logger.info("No results for: '" + product + "' with selector: " +
								selector.getClass().getSimpleName() + ", start url: " + startUrl);
					} else {
						recordCounter += appendResults(writer, results, product, selector, userAgent, startUrl);
						logger.debug(results.size() + " for: " + product + "' with selector: " +
								selector.getClass().getSimpleName() + ", start url: " + startUrl);
					}
				}
			}
		}

		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error(e);
		}
		long elapsed = (System.currentTimeMillis() - start) / 1000 / 60;
		logger.info("Total time: " + elapsed + "min . Wrote: " + recordCounter + " records.");

	}


	public int appendResults(CSVWriter writer, List<Object> results, String productName, Selector selector, String
			userAgent, URL url) {

		int records = 0;
		for (Object o : results) {
			ProductResult res = (ProductResult) o;
			/*
			"Product query", "Product name", "Country", "Search engine", "Price", "Shop name",
				"Product shop URL", "Search engine result URL", "Proxy", "User agent"
			 */
			String[] record = new String[] {
					productName, res.getProduct(), res.getCountry(), res.getSearcher(), res.getPrice(),
					res.getShop(), res.getProxy(), res.getShopURL(), url.toString(), userAgent
			};
			writer.writeNext(record);
			records++;
		}
		try {
			writer.flush();
		} catch (IOException e) {
			logger.error("CSV writer error");
			logger.error(e);
		}
		if (writer.checkError()) {
			logger.error("CSV writer error");
		}
		return records;
	}
}
