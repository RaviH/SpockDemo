package com.mayabansi.webapp.service.spock

import spock.lang.Specification
import com.mayabansi.webapp.domain.Book
import com.mayabansi.webapp.service.PromotionsService
import com.mayabansi.webapp.service.WeeklySpecialsService
import com.mayabansi.webapp.service.CustomerSpecialsService
import com.mayabansi.webapp.dao.BookDao
import org.appfuse.model.User

/**
 * Created by IntelliJ IDEA.
 * User: Ravi Hasija
 * Date: Aug 15, 2011
 * Time: 10:45:25 PM
 * To change this template use File | Settings | File Templates.
 */
class IfTimeIsAvailableShowSpec extends Specification
{
    def "In Spock First Stubbing Rules"()
    {
        setup:
            Book book1 = new Book().setTitle("Book #1");
            Book book2 = new Book().setTitle("Book #2");
            mockBookDao.get(1L) >> book1
            mockBookDao.get(1L) >> book2

        when:
            def title1 = mockBookDao.get(1L).getTitle();
            def title2 = mockBookDao.get(1L).getTitle();

        then:
            "Book #1".equals(title1)
            "Book #1".equals(title2)
    }

    /*
     -----------------------------------------
     Initialization code below
     -----------------------------------------
     */

    PromotionsService promotionsService
    User user = new User("ravi.hasija@gmail.com");

    BookDao mockBookDao = Mock();
    CustomerSpecialsService mockCustomerSpecialsService = Mock()
    WeeklySpecialsService mockWeeklySpecialsService = Mock()

    List<Book> top5BooksOnSaleList
    List<Book> booksOnSpecialPromotionList;

    def setup() {
        promotionsService = new PromotionsService();

        promotionsService.bookDao = mockBookDao
        promotionsService.customerSpecialsService = mockCustomerSpecialsService
        promotionsService.weeklySpecialsService = mockWeeklySpecialsService

        user.setId(1L);

        top5BooksOnSaleList = [
                new Book("Beautiful life", 25.00D, 2005),
                new Book("Sarasota rocks", 15.00D, 2010),
                new Book("Music gives soul", 125.00D, 2006),
                new Book("Making Java Groovy", 20.00D, 2004),
                new Book("Why we should?", 99.00D, 2009)
        ]

        booksOnSpecialPromotionList = [
                new Book("Beautiful life", 20.00D, 2005),
                new Book("Sarasota rocks", 10.00D, 2010),
                new Book("Music gives soul", 100.00D, 2006)
        ]
    }

}
