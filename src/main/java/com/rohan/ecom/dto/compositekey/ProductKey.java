package com.rohan.ecom.dto.compositekey;

import java.time.LocalDate;

public record ProductKey(
    String orderCode,
    LocalDate orderDate
) {}
