package com.mayabansi.webapp.service;

import com.mayabansi.webapp.domain.Cart;
import com.mayabansi.webapp.domain.CartItem;
import com.mayabansi.webapp.domain.MainOrder;
import org.apache.log4j.Logger;
import org.appfuse.dao.GenericDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rhasija
 * Date: Sep 25, 2010
 * Time: 11:02:33 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class OrderProcessService {

    final Logger log = Logger.getLogger(OrderProcessService.class);

    private final BigDecimal FIVE = new BigDecimal(5);
    private final BigDecimal ZERO = new BigDecimal(0);

    private GenericDao<Cart, Long> cartDao;
    private FirstStep firstStep;
    private SecondStep secondStep;
    private ThirdStep thirdStep;

    @Autowired
    public OrderProcessService setCartDao(GenericDao<Cart, Long> cartDao) {
        this.cartDao = cartDao;
        return this;
    }

    @Autowired
    public OrderProcessService setFirstStep(FirstStep firstStep) {
        this.firstStep = firstStep;
        return this;
    }

    @Autowired
    public OrderProcessService setSecondStep(SecondStep secondStep) {
        this.secondStep = secondStep;
        return this;
    }

    @Autowired
    public OrderProcessService setThirdStep(ThirdStep thirdStep) {
        this.thirdStep = thirdStep;
        return this;
    }

    public BigDecimal order(MainOrder order) {
        final BigDecimal finalTotal = order.getTotalAmount().multiply(FIVE);
        final Cart cart = cartDao.get(1L);
        final CartItem cartItem = new CartItem(3.0D, 35D, new Date(), 2, "Book");

        if (!firstStep.addToCart(cart, cartItem)) {
            return ZERO;
        }

        if (!secondStep.updateCartTotal(cart, cartItem)) {
            return ZERO;
        }

        if (!thirdStep.justOrderIt(cart)) {
            return ZERO;
        }

        return finalTotal;
    }

    public void addToCart(Cart cart) {
        if (cart == null) {
            thirdStep.addSomethingToCart(ThirdStep.ThirdStepType.NEW);
        } else {
            thirdStep.addSomethingToCart(ThirdStep.ThirdStepType.ALREADY_EXISTS);
        }

    }

    public void slowItDown() throws InterruptedException {
        log.info("Before sleeping");
        long t0 = System.currentTimeMillis();
        long millis = 100;
        long millisLeft = millis;

        while (millisLeft > 0) {
            Thread.sleep(millisLeft);
            long t1 = System.currentTimeMillis();
            millisLeft = millis - (t1 - t0);
        }
        thirdStep.someVeryImportantBusinessMethod();
    }

}
