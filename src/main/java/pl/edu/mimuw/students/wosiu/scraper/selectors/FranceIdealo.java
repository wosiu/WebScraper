package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class FranceIdealo extends IdealoSelector {

	private boolean view1Processed = false;

	public FranceIdealo() throws ConnectionException {
		super("France", "http://www.idealo.fr/", "prechcat");
	}
}
