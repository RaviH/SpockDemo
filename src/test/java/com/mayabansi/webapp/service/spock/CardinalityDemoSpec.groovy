package com.mayabansi.webapp.service.spock

import spock.lang.Specification
import org.appfuse.model.User
import com.mayabansi.webapp.service.PromotionsService
import com.mayabansi.webapp.domain.Book
import com.mayabansi.webapp.service.WeeklySpecialsService
import com.mayabansi.webapp.service.CustomerSpecialsService
import com.mayabansi.webapp.dao.BookDao

/**
 * Created by IntelliJ IDEA.
 * User: Ravi Hasija
 * Date: Aug 15, 2011
 * Time: 10:16:35 PM
 * To change this template use File | Settings | File Templates.
 */
class CardinalityDemoSpec extends Specification
{
        def "Demo of strict cardinality in spock"() {
        when:
          def list = promotionsService.getSimplePromotions(null)

        then:
            // If we do not specify a cardinality then it becomes optional
            // For ex: mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            // assumes call to getTop5BooksOnSale is optional
            1 * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
            0 * mockBookDao.getSpecialPromotionsBasedOnUser(_ as User)
    }

    def "At least n times cardinality in spock"() {
        setup:
            (1.._) * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
        when:
            def list = promotionsService.getSimplePromotions(null)
        then:
            list != null
    }

    def "At most n times cardinality in spock"() {
        setup:
            (_..2) * mockBookDao.getTop5BooksOnSale() >> top5BooksOnSaleList
        when:
            def list = promotionsService.getSimplePromotions(null)
            list = promotionsService.getSimplePromotions(null)
        then:
            list != null
    }

    PromotionsService promotionsService
    User user = new User("ravi.hasija@gmail.com");

    BookDao mockBookDao = Mock();
    CustomerSpecialsService customerSpecialsService = Mock()
    WeeklySpecialsService weeklySpecialsService = Mock()

    List<Book> top5BooksOnSaleList
    List<Book> booksOnSpecialPromotionList;

    def setup() {
        promotionsService = new PromotionsService();

        promotionsService.bookDao = mockBookDao
        promotionsService.customerSpecialsService = customerSpecialsService
        promotionsService.weeklySpecialsService = weeklySpecialsService

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
