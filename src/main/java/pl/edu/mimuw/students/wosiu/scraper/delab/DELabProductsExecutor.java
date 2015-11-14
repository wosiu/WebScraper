package pl.edu.mimuw.students.wosiu.scraper.delab;

import org.apache.log4j.Logger;
import pl.edu.mimuw.students.wosiu.scraper.Executor;
import pl.edu.mimuw.students.wosiu.scraper.Selector;

import java.net.URL;
import java.util.List;

public class DELabProductsExecutor extends Executor {

	private static final Logger logger = Logger.getLogger(DELabProductsExecutor.class);

	// private CSV builder //todo

	private static final String DEFAULT_CONFIG = "/home/m/scraper/config_full.json";

	public static void main(String[] args) {
		Executor exe = new Executor();
		String configPath;

		if (args.length != 1) {
			logger.warn("Argument missing - add config file path. Set default: " + DEFAULT_CONFIG);
			configPath = DEFAULT_CONFIG;
		} else {
			configPath = args[0];
		}

		exe.run(configPath);
	}


}
