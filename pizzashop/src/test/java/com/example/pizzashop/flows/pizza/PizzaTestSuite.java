package com.example.pizzashop.flows.pizza;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Chạy toàn bộ các luồng của chức năng Pizza.
 */
@Suite
@SuiteDisplayName("Cụm chức năng: Pizza")
@SelectPackages("com.example.pizzashop.flows.pizza")
public class PizzaTestSuite {
}
