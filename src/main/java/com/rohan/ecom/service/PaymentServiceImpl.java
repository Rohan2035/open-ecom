package com.rohan.ecom.service;

import org.springframework.stereotype.Service;

@Service("DUMMY")
public class PaymentServiceImpl implements PaymentService {

    @Override
    public String makePayment() {
        return "SUCCESS";
    }
}
