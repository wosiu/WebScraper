package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class SloveniaCeneje extends YellowSelector {

	public SloveniaCeneje() throws ConnectionException {
		super("Slovenia", "http://www.ceneje.si/", "Iskanje/Izdelki");
	}
}
