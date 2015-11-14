package pl.edu.mimuw.students.wosiu.scraper.delab;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pl.edu.mimuw.students.wosiu.scraper.ConfigException;


public class Config {

	private static final Logger logger = Logger.getLogger(Config.class);

	private List<DELabProductSelector> selectors;
	private List<String> userAgents;
	private List<String> products;

	private String outputPath;

	public boolean isCollectProxy() {
		return collectProxy;
	}

	public void setCollectProxy(boolean collectProxy) {
		this.collectProxy = collectProxy;
	}

	public boolean isRedirectShopLink() {
		return redirectShopLink;
	}

	public void setRedirectShopLink(boolean redirectShopLink) {
		this.redirectShopLink = redirectShopLink;
	}

	private boolean collectProxy = true;
	private boolean redirectShopLink = true;

	private void init() {
		selectors = new LinkedList<>();
		userAgents = new LinkedList<>();
		products = new LinkedList<>();
	}

	public Config() {
		init();
	}


	public List<DELabProductSelector> getSelectors() {
		return selectors;
	}

	public List<String> getUserAgents() {
		return userAgents;
	}

	public List<String> getProducts() {
		return products;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public Config(String JSONPath) throws ConfigException {
		readJSON(JSONPath);
	}

	public void check(Object o, String objectName, String filePath) throws ConfigException {
		if ( o == null ) {
			throw new ConfigException("Incorrect config JSON schema. Missing field: '" + objectName + "'. Check file: " +
					filePath);
		}
	}

	public void readJSON(String filePath) throws ConfigException {
		// clear prior config
		init();

		// read the json file
		FileReader reader = null;
		try {
			reader = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			throw new ConfigException("Cannot read config file: " + filePath + ". Check path if exists.");
		}

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) jsonParser.parse(reader);
		} catch (ParseException e) {
			throw new ConfigException("Incorrect config JSON schema. Check file: " + filePath);
		} catch (IOException e) {
			throw new ConfigException("Cannot read config file: " + filePath + ". Check file access.");
		}

		// get arrays from the JSON object
		JSONArray selectors = (JSONArray) jsonObject.get("selectors");
		check(selectors, "selectors", filePath);
		JSONArray products = (JSONArray) jsonObject.get("products");
		check(products, "products", filePath);
		JSONArray userAgents = (JSONArray) jsonObject.get("userAgents");
		check(userAgents, "userAgents", filePath);
		this.outputPath = (String) jsonObject.get("out");


		Boolean collectProxy = (Boolean) jsonObject.get("collectProxy");
		if (collectProxy != null) {
			this.collectProxy = collectProxy;
		}
		logger.info("Colllect proxy servers: " + this.collectProxy);

		Boolean redirectShopLink = (Boolean) jsonObject.get("redirectShopLink");
		if (redirectShopLink != null) {
			this.redirectShopLink = redirectShopLink;
		}
		logger.info("Redirect shop link: " + this.redirectShopLink);


		// create selectors
		for (Object o : selectors) {
			String str = (String) o;
			Class<?> clazz = null;
			try {
				clazz = Class.forName("pl.edu.mimuw.students.wosiu.scraper.selectors." + str);
				DELabProductSelector sel = (DELabProductSelector) clazz.newInstance();
				sel.setCollectProxy(this.collectProxy);
				sel.setRedirectShopLink(this.redirectShopLink);
				this.selectors.add(sel);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				logger.debug(e.toString());
				throw new ConfigException("Cannot create selector: " + str + ". Check typo in config.");
			}
		}

		for (Object o : userAgents) {
			String s = (String) o;
			this.userAgents.add(s);
		}

		for (Object o : products) {
			String s = (String) o;
			this.products.add(s);
		}
	}

}
