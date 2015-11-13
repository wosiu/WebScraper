package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class DenmarkKelkoo extends KelkooSelector {

	public DenmarkKelkoo() throws ConnectionException {
		super("Denmark", "http://www.kelkoo.dk/");
	}
}
