package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class IrelandPricespy extends PricespySelector {

	public IrelandPricespy() throws ConnectionException {
		super("Ireland", "http://www.pricespy.ie/");
	}
}
