package com.jack.spring.xml.service.impl;

import com.jack.spring.xml.BookShopDao;
import com.jack.spring.xml.service.BookShopService;

public class BookShopServiceImpl implements BookShopService {

	private BookShopDao bookShopDao;
	
	public void setBookShopDao(BookShopDao bookShopDao) {
		this.bookShopDao = bookShopDao;
	}
	
	@Override
	public void purchase(String username, String isbn) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		//1. ��ȡ��ĵ���
		int price = bookShopDao.findBookPriceByIsbn(isbn);
		
		//2. �������Ŀ��
		bookShopDao.updateBookStock(isbn);
		
		//3. �����û����
		bookShopDao.updateUserAccount(username, price);
	}

}
