package com.globalblue.homework.repository;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class StaticVATRateRepository implements VATRateRepository {
    @Override
    public List<BigDecimal> getSupportedVATRates() {
        return Arrays.asList(new BigDecimal("0.1"), new BigDecimal("0.13"), new BigDecimal("0.20"));
    }
}
