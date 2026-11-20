package com.example.pizzashop.flows.order;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Chạy toàn bộ các luồng của chức năng Đơn hàng.
 */
@Suite
@SuiteDisplayName("Cụm chức năng: Đơn hàng")
@SelectPackages("com.example.pizzashop.flows.order")
public class OrderTestSuite {
}
