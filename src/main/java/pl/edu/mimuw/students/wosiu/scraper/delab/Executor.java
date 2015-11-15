package pl.edu.mimuw.students.wosiu.scraper.delab;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.*;
import pl.edu.mimuw.students.wosiu.scraper.ConfigException;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;

import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Executor {

	private static final Logger logger = Logger.getLogger(Executor.class);
	private static final String LOG_LEVEL = "debug";

	public Executor() {
		log4jInit();
	}

	private static final String DEFAULT_CONFIG = "config.json";

	public static void main(String[] args) {
		Executor exe = new Executor();
		String configPath;

		if (args.length != 1) {
			logger.warn("Argument missing - add config file path. Set default: " + DEFAULT_CONFIG);
			configPath = DEFAULT_CONFIG;
		} else {
			configPath = args[0];
		}

		try {
			exe.run(configPath);
		} catch (IOException | ConfigException e) {
			logger.error(e.toString());
		}
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

	public CSVWriter createResultWriter(Config conf) throws IOException {
		CSVWriter writer = null;
		writer = new CSVWriter(new FileWriter(conf.getOutputPath()), '\t');

		// TODO enum with ProductResult attr and then build CSV respecting header with enum values
		String[] CSVheader = {"Product query", "Product name", "Country", "Search engine", "Price", "Shop name",
				"Proxy", "Product shop URL", "Search engine result URL", "User agent"};
		writer.writeNext(CSVheader);
		return writer;
	}


	public void run(String JSONconfigPath) throws IOException, ConfigException {
		long start = System.currentTimeMillis();

		Config conf = new Config(JSONconfigPath);
		CSVWriter writerResult = createResultWriter(conf);
		CSVWriter writerEmpty = new CSVWriter(new FileWriter("empty.csv"), '\t');

		int recordCounter = 0;
		int selectorsNum = conf.getSelectors().size();
		int productsNum = conf.getProducts().size();

		logger.info("Scraping for " + selectorsNum + " selectors.");
		for (String userAgent : conf.getUserAgents()) {
			for (String product : conf.getProducts()) {
				for (Selector selector : conf.getSelectors()) {
					URL startUrl = null;
					try {
						startUrl = selector.prepareTargetUrl(product);
					} catch (ConnectionException e) {
						logger.error("Cannot process product - url malformed: " + product + ", selector: " + selector
								.getClass().getSimpleName());
						logger.error(e.toString());
						continue;
					}
					// traverse through pagination
					List<Object> results = null;
					results = selector.traverseAndCollectProducts(userAgent, startUrl);
					if (results.isEmpty()) {
						appendEmpty(writerEmpty, product, selector, userAgent, startUrl);
						logger.info("No results for: '" + product + "' with selector: " +
								selector.getClass().getSimpleName() + ", start url: " + startUrl);
					} else {
						recordCounter += appendResults(writerResult, results, product, selector, userAgent, startUrl);
						logger.debug(results.size() + " for: " + product + "' with selector: " +
								selector.getClass().getSimpleName() + ", start url: " + startUrl);
					}
				}
			}
		}

		writerResult.close();


		long elapsed = (System.currentTimeMillis() - start) / 1000 / 60;
		System.out.println("total time:\trecords:\tcountries\tproducts\tproxy\tredirect" );
		System.out.println(StringUtils.join(new Object[]{elapsed, recordCounter, selectorsNum, productsNum, conf
				.isCollectProxy(), conf.isRedirectShopLink()}, "\t"));
	}


	public int appendResults(CSVWriter writer, List<Object> results, String productName, Selector selector, String
			userAgent, URL url) {

		int records = 0;
		for (Object o : results) {
			ProductResult res = (ProductResult) o;
			String[] record = Arrays.asList(
					productName, res.getProduct(), res.getCountry(), res.getSearcher(), res.getPrice(), res.getShop(),
					res.getProxy(), res.getShopURL(), url.toString(), userAgent).
					stream().map(s -> (s==null) ? "" : s.trim()).collect(Collectors.toList()).
					toArray(new String[]{});

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

	public void appendEmpty(CSVWriter writer, String productName, Selector selector, String userAgent, URL url) {
			String proxy = (selector.getLastUsedProxy() == null) ? "LOCAL" : selector.getLastUsedProxy().toString();
			String[] record = Arrays.asList(selector.getCountry(), selector.getSourceURL().toString(), productName, url.toString
					(), proxy, userAgent).stream().map(String::trim).collect(Collectors.toList()).toArray(new String[]{});

		writer.writeNext(record);
		try {
			writer.flush();
		} catch (IOException e) {
			logger.error("CSV writer error");
			logger.error(e);
		}
		if (writer.checkError()) {
			logger.error("CSV writer error");
		}
	}
}
