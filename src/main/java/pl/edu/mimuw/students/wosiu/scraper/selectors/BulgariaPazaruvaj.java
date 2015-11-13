package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class BulgariaPazaruvaj extends OrangeSelector {

	public BulgariaPazaruvaj() throws ConnectionException {
		super("Bulgaria", "http://www.pazaruvaj.com/");
	}
}
