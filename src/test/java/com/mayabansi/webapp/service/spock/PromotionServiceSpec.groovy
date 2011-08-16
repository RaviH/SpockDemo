package com.mayabansi.webapp.service.spock

import com.mayabansi.webapp.dao.BookDao
import com.mayabansi.webapp.domain.Book
import com.mayabansi.webapp.service.CustomerSpecialsService
import com.mayabansi.webapp.service.PromotionsService
import com.mayabansi.webapp.service.WeeklySpecialsService
import org.appfuse.model.User
import spock.lang.Specification
import spock.lang.Timeout

/**
 * Created by IntelliJ IDEA.
 * User: rhasija
 * Date: Sep 26, 2010
 * Time: 1:59:30 PM
 * To change this template use File | Settings | File Templates.
 */
class PromotionServiceSpec extends Specification {

    def "Simple example to demo the anatomy of Spock"() {
        setup:
            int a = 10
            int b = 20
        when:
            int c = Math.min(a,b)
        then:
            c == 10        
    }

    def "Show How Stubbing Works in Spock"() {
        setup:
            1 * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * mockBookDao.getSpecialPromotionsBasedOnUser(_ as User)

        when: "The user has not logged in, show the top 5 books on Sale"
            def list = promotionsService.getSimplePromotions(null)

        then: "The returned list from the service should be the same as the one stubbed into the mockBookDao"
            top5BooksOnSaleList == list
    }


    def "Iterative Stubbing"()
    {
        setup: "When mockBookDao is called first then return Book #1, then return Book #2"
            Book book1 = new Book().setTitle("Book #1");
            Book book2 = new Book().setTitle("Book #2");

            2 * mockBookDao.get(1L) >>> [book1, book2];
        
        when:
            def title1 = mockBookDao.get(1L).getTitle();
            def title2 = mockBookDao.get(1L).getTitle();

        then:
            "Book #1".equals(title1)
            "Book #2".equals(title2)
    }



    def "Stub Exceptions in void and non void mock methods. Also demo multiple when then blocks."()
    {
        given:
            1 * mockBookDao.get(1L) >> {throw new RuntimeException()}
            1 * mockBookDao.remove(1L) >> { throw new RuntimeException() }
        when:
            mockBookDao.get(1L)
        then:
            thrown(RuntimeException)

        when:
            mockBookDao.remove(1L)
        
        then:
            thrown(RuntimeException)
    }

    @Timeout(2)
    def "Timeout demo"() {
        setup:
            BigDecimal bd = new BigDecimal(5)
        when:
            bd = bd.multiply(5)
            Thread.sleep(1000) // make this 3 during presentation
        then:
            bd == 25
    }

    def "Get Promotions is passed a null user"() {
        setup:
            1 * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * mockBookDao.getSpecialPromotionsBasedOnUser(null) >> booksOnSpecialPromotionList
            0 * mockCustomerSpecialsService.applySpecials()
            1 * mockCustomerSpecialsService.getSpecials() >> booksOnSpecialPromotionList

        when:
            final List<Book> promotionList = promotionsService.getPromotions(null);

        then:
            promotionList.size() == 3
    }

    def "Get Promotions is passed a non null user"() {
        setup: "Setting up with dummy data"
            0 * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            1 * mockBookDao.getSpecialPromotionsBasedOnUser(user) >> booksOnSpecialPromotionList
            1 * mockCustomerSpecialsService.applySpecials(_, _) >> booksOnSpecialPromotionList
            0 * mockCustomerSpecialsService.getSpecials();

        when: "There is a user in the HTTP Session"
            final List<Book> promotionList = promotionsService.getPromotions(user);

        then: "Size of the list should be as expected"
            promotionList.size() == 6
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