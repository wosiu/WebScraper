package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class ItalyKelkoo extends KelkooSelector {

	public ItalyKelkoo() throws ConnectionException {
		super("Italy", "http://www.kelkoo.it/");
	}
}
