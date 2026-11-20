package com.example.pizzashop.flows;

import com.example.pizzashop.flows.kafka.KafkaTestSuite;
import com.example.pizzashop.flows.order.OrderTestSuite;
import com.example.pizzashop.flows.pizza.PizzaTestSuite;
import com.example.pizzashop.flows.tablebooking.TableBookingTestSuite;
import com.example.pizzashop.flows.user.UserTestSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Chạy tất cả các cụm chức năng (mỗi cụm gồm các luồng của 1 chức năng).
 *
 * Chạy: mvnw test -Dtest=AllTests
 */
@Suite
@SuiteDisplayName("PizzaShop - Tất cả các cụm chức năng")
@SelectClasses({
        UserTestSuite.class,
        PizzaTestSuite.class,
        OrderTestSuite.class,
        TableBookingTestSuite.class,
        KafkaTestSuite.class
})
public class AllTests {
}
