package com.globalblue.homework.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class VATNetGrossDistribution {
    private BigDecimal vatRate;
    private BigDecimal vatValue;
    private BigDecimal netAmount;
    private BigDecimal grossAmount;
}
