package com.example.pizzashop.flows.kafka;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Chạy toàn bộ các luồng của chức năng Kafka.
 */
@Suite
@SuiteDisplayName("Cụm chức năng: Kafka")
@SelectPackages("com.example.pizzashop.flows.kafka")
public class KafkaTestSuite {
}
