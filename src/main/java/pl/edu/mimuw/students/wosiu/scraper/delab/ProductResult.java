package pl.edu.mimuw.students.wosiu.scraper.delab;
import java.net.Proxy;
import java.util.*;

public class ProductResult {
	private Map<String, Object> fields;

	public ProductResult() {
		fields = new HashMap<>();
	}
	
	public void setCountry(Object o) 	{ fields.put("country", o);}
	public void setSearcher(String o) 	{ fields.put("searcher", o);}
	public void setProduct(String o) 	{ fields.put("product", o);}
	// TODO rename setSearchListURL
	public void setSearchURL(String o) 	{ fields.put("search-url", o);}
	public void setPrice (Object o) 	{ fields.put("price", o);}
	// TODO rename setShopName
	public void setShop(String o) 		{ fields.put("shop", o);}
	// TODO rename setProductURL
	public void setShopURL(String o) 	{ fields.put("shop-url", o);}
	public void setTime(long o) 		{ fields.put("time", o);}
	public void setTime() 				{ fields.put("time", new Date().getTime()); }
	public void setUserAgent(Object o) 	{ fields.put("user-agent", o);}
	public void setProxy(Proxy proxy) {
		if (proxy == null) {
			fields.put("proxy", "LOCAL");
		} else {
			fields.put("proxy", proxy.toString());
		}
	}
	public void setPercentageShopRating(int percentage) { fields.put("shop-rating", percentage);}
	public void setShopClientsNum(int num) { fields.put("shop-clients-num", num);}
	public void setDeliveryCost(int cost) 	{ fields.put("delivery-cost", cost); }


	public String getCountry() 	{ return safeGet("country"); }
	public String getSearcher() 	{ return safeGet("searcher"); }
	public String getProduct() 	{ return safeGet("product"); }
	// TODO rename getSearchListURL
	public String getSearchURL() 	{ return safeGet("search-url"); }
	public String getPrice () 	{ return safeGet("price"); }
	// TODO rename getShopName
	public String getShop() 		{ return safeGet("shop"); }
	// TODO rename getProductURL
	public String getShopURL() 	{ return safeGet("shop-url"); }
	public String getTime() 	{ 
		// TODO format time
		return safeGet("time"); 
	}
	public String getUserAgent() 	{ return safeGet("user-agent"); }
	public String getProxy() 	{ return safeGet("proxy"); }
	public String getPercentageShopRating() { return safeGet("shop-rating"); }
	public String getShopClientsNum() { return safeGet("shop-clients-num"); }
	public String getDeliveryCost() 	{ return safeGet("delivery-cost"); }




	public void set (String key, Object value) { fields.put(key, value);}

	public String safeGet(String key) {
		Object f = fields.get(key);
		if (f == null) {
			return "";
		} else {
			return f.toString();
		}
	}

	@Override
	public String toString() {
		return fields.toString();
	}
}
