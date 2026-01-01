package com.rohan.inventory.dto.compositekey;

import java.time.LocalDate;

public record OrderKey(
    String orderCode,
    LocalDate orderDate
) {}
