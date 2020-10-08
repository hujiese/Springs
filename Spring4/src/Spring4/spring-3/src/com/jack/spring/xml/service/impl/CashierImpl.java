package com.jack.spring.xml.service.impl;


import com.jack.spring.tx.Cashier;
import com.jack.spring.xml.service.BookShopService;

import java.util.List;

public class CashierImpl implements Cashier {

	private BookShopService bookShopService;
	
	public void setBookShopService(BookShopService bookShopService) {
		this.bookShopService = bookShopService;
	}
	
	@Override
	public void checkout(String username, List<String> isbns) {
		for(String isbn: isbns){
			bookShopService.purchase(username, isbn);
		}
	}

}
