package com.example.pizzashop.flows.user;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Chạy toàn bộ các luồng của chức năng Người dùng.
 */
@Suite
@SuiteDisplayName("Cụm chức năng: Người dùng")
@SelectPackages("com.example.pizzashop.flows.user")
public class UserTestSuite {
}
