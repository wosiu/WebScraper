package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class RomaniaCompari extends OrangeSelector {

	public RomaniaCompari() throws ConnectionException {
		super("Romania", "http://www.compari.ro/");
	}
}
