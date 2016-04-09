package pl.edu.mimuw.students.wosiu.scraper.alexa;

import org.apache.log4j.Logger;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import java.net.URL;
import java.util.List;

public class TranslateExecutor {
	private static final Logger logger = Logger.getLogger(TranslateExecutor.class);
	private static Selector selector = null;

	public TranslateExecutor() throws ConnectionException {
		selector = new DikiSelector();
	}

	public String translate(String singlePhrase) {
		URL startUrl = null;

		try {
			startUrl = selector.prepareTargetUrl(singlePhrase);
		} catch (ConnectionException e) {
			logger.error("Cannot process product - url malformed: " + singlePhrase + ", selector: " + selector
					.getClass().getSimpleName());
			logger.error(e.toString());
			return null;
		}
		// traverse through pagination
		String translation = null;
		List<Object> results = selector.traverseAndCollectProducts(Utils.USER_AGENT, startUrl);
		if (results.isEmpty()) {
			// TODO split phrase by whitespaces and translate separate words
			logger.info("No results for: '" + singlePhrase + "' with selector: " +
					selector.getClass().getSimpleName() + ", start url: " + startUrl);
		} else {
			logger.debug(results.size() + " for: " + singlePhrase + "' with selector: " +
					selector.getClass().getSimpleName() + ", start url: " + startUrl);
			translation = (String) results.get(0);

		}
		return translation;
	}
}
