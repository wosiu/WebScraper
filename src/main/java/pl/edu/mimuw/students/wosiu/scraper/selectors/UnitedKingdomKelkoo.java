package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class UnitedKingdomKelkoo extends KelkooSelector {

	public UnitedKingdomKelkoo() throws ConnectionException {
		super("United Kingdom", "http://www.kelkoo.co.uk/");
	}
}
