package com.rohan.inventory.dto.compositekey;

import java.time.LocalDate;

public record ProductKey(
    String orderCode,
    LocalDate orderDate
) {}
