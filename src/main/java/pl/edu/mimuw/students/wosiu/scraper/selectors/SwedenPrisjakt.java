package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;
import pl.edu.mimuw.students.wosiu.scraper.ProxyFinder;

import java.util.Collection;

public class SwedenPrisjakt extends PricespySelector {

	public SwedenPrisjakt() throws ConnectionException {
		super("Sweden", "http://www.prisjakt.nu/");
	}
}
