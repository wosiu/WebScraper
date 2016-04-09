package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class HungaryArukereso extends OrangeSelector {

	public HungaryArukereso() throws ConnectionException {
		super("Hungary", "http://www.arukereso.hu/");
	}
}
