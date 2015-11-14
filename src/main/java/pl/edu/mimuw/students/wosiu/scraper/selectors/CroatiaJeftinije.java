package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class CroatiaJeftinije extends YellowSelector {

	public CroatiaJeftinije() throws ConnectionException {
		super("Croatia", "http://www.jeftinije.hr/", "Trazenje/Proizvodi");
	}
}
