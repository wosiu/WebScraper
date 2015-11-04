package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.*;

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
		Config conf;
		try {
			conf = new Config(JSONconfigPath);
		} catch (ConfigException e) {
			logger.error(e.toString());
			return;
		}

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
					try {
						results = selector.traverseAndCollectProducts(userAgent, startUrl);
					} catch (ConnectionException e) {
						logger.error("Cannot process product: " + product + " with selector: " +
								getClass().getSimpleName() + ", start url: " + startUrl);
						logger.debug(e.toString());
						break;
					}

					buildResult(results, product, selector, userAgent, startUrl);
				}
			}
		}
	}


	public void buildResult(List<Object> results, String productName, Selector selector, String userAgent, URL url) {
		for (Object o : results) {
			System.out.println(o.toString());
		}
	}
}
