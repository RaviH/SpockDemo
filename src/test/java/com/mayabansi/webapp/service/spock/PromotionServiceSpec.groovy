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

    def "Show How Stubbing Works in Spock"() {
        setup:
            1 * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * bookDao.getSpecialPromotionsBasedOnUser(_ as User)

        when:
            def list = promotionsService.getSimplePromotions(null)

        then:
            list.size() == 5
    }


    def "Iterative Stubbing"() {
        setup:
            Book book1 = new Book().setTitle("Book #1");
            Book book2 = new Book().setTitle("Book #2");
            2 * bookDao.get(1L) >>> [book1, book2];
        
        when:
            def title1 = bookDao.get(1L).getTitle();
            def title2 = bookDao.get(1L).getTitle();
        
        then:
            "Book #1".equals(title1)
            "Book #2".equals(title2)
    }

    def "In Spock First Stubbing Rules"() {
        setup:
            Book book1 = new Book().setTitle("Book #1");
            Book book2 = new Book().setTitle("Book #2");
            2 * bookDao.get(1L) >> book1
            bookDao.get(1L) >> book2

        when:
            def title1 = bookDao.get(1L).getTitle();
            def title2 = bookDao.get(1L).getTitle();

        then:
            "Book #1".equals(title1)
            "Book #1".equals(title2)
    }

    def "Stub Exceptions in void and non void mock methods"() {
        given:
            1 * bookDao.get(1L) >> {throw new RuntimeException()}
            1 * bookDao.remove(1L) >> { throw new RuntimeException() }
        when:
            bookDao.get(1L)
        then:
            thrown(RuntimeException)

        when:
            bookDao.remove(1L)
        
        then:
            thrown(RuntimeException)
    }

    def "Demo of strict cardinality in spock"() {
        when:
          def list = promotionsService.getSimplePromotions(null)
        then:
            // If we do not specify a cardinality then it becomes optional
            // For ex: bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            // assumes call to getTop5BooksOnSale is optional
            1 * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * bookDao.getSpecialPromotionsBasedOnUser(_ as User)
    }

    def "At least n times cardinality in spock"() {
        setup:
            (1.._) * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
        when:
            def list = promotionsService.getSimplePromotions(null)
        then:
            list != null
    }

    def "At most n times cardinality in spock"() {
        setup:
            (_..2) * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
        when:
            def list = promotionsService.getSimplePromotions(null)
            list = promotionsService.getSimplePromotions(null)
        then:
            list != null
    }

    def "Show how where works"(Book a, String titleExpected) {
        setup:
            1 * bookDao.get(1L) >> { a };

        when:
            def title1 = bookDao.get(1L).getTitle();

        then:
            titleExpected.equals(title1)

        where:
            a << [new Book(title: "Book #1"), new Book(title: "Book #2"), new Book(title: "Book #1")]
            titleExpected << ["Book #1", "Book #2", "Book #1"]
    }


    def "Show how data provider works"(Book a, String titleExpected) {
        setup:
            1 * bookDao.get(1L) >> { a };

        when:
            def title1 = bookDao.get(1L).getTitle();

        then:
            titleExpected.equals(title1)

        where:
            a                           | titleExpected
            new Book(title: "Book #1")  | "Book #1"
            new Book(title: "Book #2")  | "Book #2"
            new Book(title: "Book #1")  | "Book #1"
    }
     
    def "computing the maximum of two numbers **Demo expect and where**"(int a, int b, int c) {
        expect:
            Math.max(a, b) == c

        where:
            a << [5, 3]
            b << [1, 9]
            c << [5, 9]
    }

    @Timeout(2) // make this 1 during presentation 
    def "Timeout demo"() {
        setup:
            BigDecimal bd = new BigDecimal(5)
        when:
            bd = bd.multiply(5)
            Thread.sleep(1000)
        then:
            bd == 25
    }

    def "Get Promotions is passed a null user"() {
        setup:
            1 * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * bookDao.getSpecialPromotionsBasedOnUser(null) >> booksOnSpecialPromotionList
            0 * customerSpecialsService.applySpecials()
            1 * customerSpecialsService.getSpecials() >> booksOnSpecialPromotionList

        when:
            final List<Book> promotionList = promotionsService.getPromotions(null);

        then:
            promotionList.size() == 3
    }

    def "Get Promotions is passed a non null user"() {
        setup: "Setting up with dummy data"
            0 * bookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            1 * bookDao.getSpecialPromotionsBasedOnUser(user) >> booksOnSpecialPromotionList
            1 * customerSpecialsService.applySpecials(_, _) >> booksOnSpecialPromotionList
            0 * customerSpecialsService.getSpecials();

        when: "There is a user in the HTTP Session"
            final List<Book> promotionList = promotionsService.getPromotions(user);

        then: "Size of the list should be as expected"
            promotionList.size() == 6
    }

    PromotionsService promotionsService
    User user = new User("ravi.hasija@gmail.com");

    BookDao bookDao = Mock();
    CustomerSpecialsService customerSpecialsService = Mock()
    WeeklySpecialsService weeklySpecialsService = Mock()

    List<Book> top5BooksOnSaleList
    List<Book> booksOnSpecialPromotionList;

    def setup() {
        promotionsService = new PromotionsService();

        promotionsService.bookDao = bookDao
        promotionsService.customerSpecialsService = customerSpecialsService
        promotionsService.weeklySpecialsService = weeklySpecialsService

        user.setId(1L);

        top5BooksOnSaleList = [
                new Book("Beautiful life", 25.00D, 2005),
                new Book("Sarasota rocks", 15.00D, 2010),
                new Book("Music gives soul", 125.00D, 2006),
                new Book("Mocking using Mockito", 20.00D, 2004),
                new Book("Why we should?", 99.00D, 2009)
        ]

        booksOnSpecialPromotionList = [
                new Book("Beautiful life", 20.00D, 2005),
                new Book("Sarasota rocks", 10.00D, 2010),
                new Book("Music gives soul", 100.00D, 2006)
        ]
    }
    
}