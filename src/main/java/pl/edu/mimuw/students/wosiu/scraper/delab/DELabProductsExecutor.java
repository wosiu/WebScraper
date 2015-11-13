package pl.edu.mimuw.students.wosiu.scraper.delab;

import org.apache.log4j.Logger;
import pl.edu.mimuw.students.wosiu.scraper.Executor;
import pl.edu.mimuw.students.wosiu.scraper.Selector;

import java.net.URL;
import java.util.List;

public class DELabProductsExecutor extends Executor {

	private static final Logger logger = Logger.getLogger(DELabProductsExecutor.class);

	// private CSV builder //todo

	public static void main(String[] args) {
		Executor exe = new Executor();
		if (args.length != 1) {
			logger.error("Argument missing - add config file path.");
			//return; //TODO
		}
		//String configPath = args[0];
		String configPath = "/home/m/scraper/config.json";
		exe.run(configPath);

		// TODO create CSV
	}


}
