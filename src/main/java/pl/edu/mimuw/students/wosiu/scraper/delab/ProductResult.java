package pl.edu.mimuw.students.wosiu.scraper.delab;
import java.util.*;

public class ProductResult {
	private Map<String, Object> fields;

	public ProductResult() {
		fields = new HashMap<>();
	}
	
	public void setCountry(Object o) { fields.put("country", o);}
	public void setSearcher(Object o) { fields.put("searcher", o);}
	public void setProduct(Object o) { fields.put("product", o);}
	public void setSearchURL(Object o) { fields.put("search-url", o);}
	public void setPrice (Object o) { fields.put("price", o);}
	public void setShop(Object o) { fields.put("shop", o);}
	public void setShopURL(Object o) { fields.put("shop-url", o);}
	public void setTime(Object o) { fields.put("time", o);}
	public void setUserAgent(Object o) { fields.put("user-agent", o);}
	
}
