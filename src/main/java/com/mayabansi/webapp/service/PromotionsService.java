package com.mayabansi.webapp.service;

import com.mayabansi.webapp.dao.BookDao;
import com.mayabansi.webapp.domain.Book;
import org.appfuse.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rhasija
 * Date: Sep 19, 2010
 * Time: 1:22:08 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PromotionsService {

    BookDao bookDao;
    CustomerSpecialsService customerSpecialsService;
    WeeklySpecialsService weeklySpecialsService;

    @Autowired
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Autowired
    public void setWeeklySpecialsService(WeeklySpecialsService weeklySpecialsService) {
        this.weeklySpecialsService = weeklySpecialsService;
    }

    @Autowired
    public void setCustomerSpecialsService(CustomerSpecialsService customerSpecialsService) {
        this.customerSpecialsService = customerSpecialsService;
    }

    public List<Book> getSimplePromotions(final User user) {
        List<Book> bookList = new ArrayList<Book>();

        if (user == null) {
            bookList = bookDao.getTop5BooksOnSale();
        } else {
            bookList = bookDao.getSpecialPromotionsBasedOnUser(user);
        }

        return bookList;
    }

    public List<Book> getPromotions(final User user) {
        List<Book> bookList = new ArrayList<Book>();

        if (user == null) {
            bookList = bookDao.getTop5BooksOnSale();
        } else {
            bookList = bookDao.getSpecialPromotionsBasedOnUser(user);
        }

        if (user == null) {
            bookList = customerSpecialsService.getSpecials();
        } else {
            bookList.addAll(customerSpecialsService.applySpecials(bookList, user));
        }

        weeklySpecialsService.applyWeeklySpecials(bookList);

        return bookList;
    }

}
