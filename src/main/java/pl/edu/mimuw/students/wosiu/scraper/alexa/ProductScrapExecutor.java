package pl.edu.mimuw.students.wosiu.scraper.alexa;

import pl.edu.mimuw.students.wosiu.scraper.ProductResult;

import java.util.ArrayList;
import java.util.List;

public class ProductScrapExecutor {

	public List<ProductResult> scrap(String productName) {
		List<ProductResult> offers = new ArrayList<>();

		// TODO, this is mock:
		ProductResult productResult = new ProductResult();
		productResult.setProduct(productName);
		productResult.setPrice(13.31);
		productResult.setShop("tesco");
		productResult.setShopURL("http://ezakupy.tesco.pl/pl-PL/ProductDetail/ProductDetail/2003120112699");
		offers.add(productResult);

		return offers;
	}

}
