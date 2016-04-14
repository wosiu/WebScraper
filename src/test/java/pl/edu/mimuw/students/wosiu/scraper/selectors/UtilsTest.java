package pl.edu.mimuw.students.wosiu.scraper.selectors;

import org.junit.Test;
import pl.edu.mimuw.students.wosiu.scraper.Utils;

import static org.junit.Assert.assertTrue;

public class UtilsTest {

	@Test
	public void testConvertSi() throws Exception {
		Utils.NumUnits res = Utils.convertToSI(" 500 g.");
		assertTrue(res.number == 0.5);
		assertTrue("kg".equals(res.unit));
	}
}
