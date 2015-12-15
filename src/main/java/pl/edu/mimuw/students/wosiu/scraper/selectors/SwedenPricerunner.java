package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class SwedenPricerunner extends PricerunnerSelector {

	public SwedenPricerunner() throws ConnectionException {
		super("Sweden", "http://www.pricerunner.se/", "Info om ");
	}
}
