package com.mayabansi.webapp.service.spock

import spock.lang.Specification
import com.mayabansi.webapp.domain.Book
import com.mayabansi.webapp.service.PromotionsService
import com.mayabansi.webapp.service.CustomerSpecialsService
import com.mayabansi.webapp.dao.BookDao
import org.appfuse.model.User
import com.mayabansi.webapp.service.WeeklySpecialsService

/**
 * Created by IntelliJ IDEA.
 * User: Ravi Hasija
 * Date: Aug 15, 2011
 * Time: 10:50:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ParametrizedMethodsSpec extends Specification
{
   def "Show how 'where' works"(Book a, String titleExpected)
   {
        setup:
            1 * mockBookDao.get(1L) >> { a };

        when:
            def title1 = mockBookDao.get(1L).getTitle();

        then:
            titleExpected.equals(title1)

        where:
            a << [new Book(title: "Book #1"), new Book(title: "Book #2"), new Book(title: "Book #1")]
            titleExpected << ["Book #1", "Book #2", "Book #1"]
    }


    def "Show how data provider works"(Book a, String titleExpected)
    {
        setup:
            1 * mockBookDao.get(1L) >> { a };

        when:
            def title1 = mockBookDao.get(1L).getTitle();

        then:
            titleExpected.equals(title1)

        where:
            a                           | titleExpected
            new Book(title: "Book #1")  | "Book #1"
            new Book(title: "Book #2")  | "Book #2"
            new Book(title: "Book #3")  | "Book #3"
    }

    def "computing the maximum of two numbers **Demo expect and where**"(int a, int b, int c)
    {
        expect:
            Math.max(a, b) == c

        where:
            a << [5, 3]
            b << [1, 9]
            c << [5, 9]
    }


    
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
