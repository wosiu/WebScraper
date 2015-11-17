package pl.edu.mimuw.students.wosiu.scraper.selectors;

import pl.edu.mimuw.students.wosiu.scraper.ConnectionException;

public class SpainIdealo extends IdealoSelector {

	private boolean view1Processed = false;

	public SpainIdealo() throws ConnectionException {
		super("Spain", "http://www.idealo.es/", "resultados");
	}
}
