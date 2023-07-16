package com.globalblue.homework.repository;

import java.math.BigDecimal;
import java.util.List;

public interface VATRateRepository {
    List<BigDecimal> getSupportedVATRates();
}
