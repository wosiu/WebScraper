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

	@Override
	public void buildResult(List<Object> results, String productName, Selector selector, String userAgent, URL url) {
		for (Object o : results) {
			System.out.println(o);
			/*
			todo build csv
			ProductResult record = (ProductResult) o;
			record.setCountry(selector.getCountry());
			record.setBrowser(selector.getBrowserURL().toString());
			record.setProductName(productName);
			record.setUserAgent(userAgent);
			record.set
					//TODO
			summarize.addRecord();
			*/
			//kraj | przeglądarka | przedmiot | wysłane zapytanie | cena | sklep internetowy | url docelowy
			//		(produkt+sklep) | data + godzina pobrania danych | przeglądarka internetowa/system operacyjny
			//który udajemy
		}
	}

}
