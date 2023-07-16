package com.globalblue.homework.model;


import com.globalblue.homework.usecase.calculator.ProvidedValue;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

@Getter
@Builder
public class GetVATNetGrossDistributionRequestDTO {
    private BigDecimal vatRate;
    private Optional<BigDecimal> netAmount;
    private Optional<BigDecimal> grossAmount;
    private Optional<BigDecimal> vatAmount;

    public ProvidedValue getProvidedValue() {
        if (netAmount.isPresent()) {
            return ProvidedValue.NET;
        } else if (grossAmount.isPresent()) {
            return ProvidedValue.GROSS;
        } else if (vatAmount.isPresent()) {
            return ProvidedValue.VAT;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public BigDecimal getValue() {
        return netAmount.orElseGet(() -> grossAmount.orElseGet(() -> vatAmount.orElseThrow()));
    }
}
