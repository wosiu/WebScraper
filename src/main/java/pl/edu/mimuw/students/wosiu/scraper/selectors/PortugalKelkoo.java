package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class PortugalKelkoo extends KelkooSelector {

	public PortugalKelkoo() throws ConnectionException {
		super("Portugal", "http://www.kelkoo.com.pt/");
	}
}
