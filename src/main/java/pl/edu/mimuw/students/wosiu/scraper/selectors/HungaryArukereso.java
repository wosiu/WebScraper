package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;
import pl.edu.mimuw.students.wosiu.scraper.Selector;
import pl.edu.mimuw.students.wosiu.scraper.Utils;
import pl.edu.mimuw.students.wosiu.scraper.delab.ProductResult;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class HungaryArukereso extends OrangeSelector {

	public HungaryArukereso() throws ConnectionException {
		super();
		setCountry("Hungary");
		setSource("http://www.arukereso.hu/");
		Collection proxies = ProxyFinder.getProxies("Hungary");
		if (proxies == null || proxies.isEmpty() ) {
			logger.debug("No proxy in ProxyFinder");
		} else {
			addAllProxies(proxies);
		}
	}
}
