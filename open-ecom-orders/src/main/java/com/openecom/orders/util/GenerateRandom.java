package com.openecom.orders.util;

import java.util.concurrent.ThreadLocalRandom;

public class GenerateRandom {

    private static String orderCode() {
        long timestamp = System.currentTimeMillis();
        int randomDigits = ThreadLocalRandom.current().nextInt(100, 1000);

        return "ORD-" + timestamp + "-" + randomDigits;
    }
}
