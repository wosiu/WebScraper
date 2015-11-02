package pl.edu.mimuw.students.wosiu.scraper;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import java.net.*;
import java.util.List;

public class Executor {

	private static final Logger logger = Logger.getLogger(Executor.class);

	public static void main(String[] args) {
		Executor exe = new Executor();
		if (args.length != 1) {
			logger.error("Argument missing - add config file path.");
			return;
		}
		exe.run(args[0]);
	}

	public void run(String JSONconfigPath) {
		Config conf;
		try {
			conf = new Config(JSONconfigPath);
		} catch (ConfigException e) {
			logger.error(e.getMessage());
			return;
		}

		for (String userAgent : conf.getUserAgents()) {
			for (String product : conf.getProducts()) {
				for (Selector selector : conf.getSelectors()) {
					URL startUrl = selector.prepareTargetUrl(product);
					// traverse through pagination
					for (URL targetURL = startUrl; targetURL != null; ) {
						Document doc = null;
						try {
							doc = selector.getDoc(userAgent, targetURL);
						} catch (ConnectionException e) {
							logger.error("Cannot process product:" + product + " with userAgent: " + userAgent +
									", selector: " + selector.getClass().getSimpleName());
							logger.error(e.getMessage());
							break;
						}
						List<Object> results = selector.getProducts(doc);
						buildResult(results, product, selector, userAgent, targetURL);
						targetURL = selector.getNextPage(doc);
					}
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
