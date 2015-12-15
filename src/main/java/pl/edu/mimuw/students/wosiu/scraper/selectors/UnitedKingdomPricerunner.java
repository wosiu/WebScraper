package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class UnitedKingdomPricerunner extends PricerunnerSelector {

	public UnitedKingdomPricerunner() throws ConnectionException {
		super("United Kingdom", "http://www.pricerunner.co.uk/", "Info on ");
	}
}
