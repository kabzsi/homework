package com.globalblue.homework.usecase.calculator;

import com.globalblue.homework.model.VATNetGrossDistribution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.BiFunction;
import java.util.function.Function;

public class VATNetGrossDistributionCalculator implements Function<BigDecimal, VATNetGrossDistribution> {
    private final BigDecimal vatRate;
    private final BiFunction<BigDecimal, BigDecimal, VATNetGrossDistribution> calculator;

    private VATNetGrossDistributionCalculator(BigDecimal vatRate, BiFunction<BigDecimal, BigDecimal, VATNetGrossDistribution> calculator) {
        this.vatRate = vatRate;
        this.calculator = calculator;
    }

    public static VATNetGrossDistributionCalculator getCalculator(BigDecimal vatRate, ProvidedValue type) {
        return switch (type) {
            case NET ->
                    new VATNetGrossDistributionCalculator(vatRate, VATNetGrossDistributionCalculator::calculateFromNet);
            case GROSS ->
                    new VATNetGrossDistributionCalculator(vatRate, VATNetGrossDistributionCalculator::calculateFromGross);
            case VAT ->
                    new VATNetGrossDistributionCalculator(vatRate, VATNetGrossDistributionCalculator::calculateFromVat);
        };
    }

    private static VATNetGrossDistribution calculateFromVat(BigDecimal vat, BigDecimal vatRate) {
        BigDecimal netAmount = vat.divide(vatRate, 10, RoundingMode.HALF_DOWN);
        return VATNetGrossDistribution.builder().vatRate(vatRate)
                .vatValue(vat.setScale(2, RoundingMode.HALF_DOWN))
                .netAmount(netAmount.setScale(2, RoundingMode.HALF_DOWN))
                .grossAmount(vat.add(netAmount).setScale(2, RoundingMode.HALF_DOWN)).build();
    }

    private static VATNetGrossDistribution calculateFromNet(BigDecimal net, BigDecimal vatRate) {
        BigDecimal vatValue = net.multiply(vatRate);
        return VATNetGrossDistribution.builder().vatRate(vatRate)
                .netAmount(net.setScale(2, RoundingMode.HALF_DOWN))
                .vatValue(vatValue.setScale(2, RoundingMode.HALF_DOWN))
                .grossAmount(net.add(vatValue).setScale(2, RoundingMode.HALF_DOWN)).build();
    }

    private static VATNetGrossDistribution calculateFromGross(BigDecimal gross, BigDecimal vatRate) {
        BigDecimal netAmount = gross.divide(BigDecimal.ONE.add(vatRate), 10, RoundingMode.HALF_DOWN);
        return VATNetGrossDistribution.builder().vatRate(vatRate)
                .grossAmount(gross.setScale(2, RoundingMode.HALF_DOWN))
                .netAmount(netAmount.setScale(2, RoundingMode.HALF_DOWN))
                .vatValue(gross.subtract(netAmount).setScale(2, RoundingMode.HALF_DOWN)).build();
    }

    @Override
    public VATNetGrossDistribution apply(BigDecimal value) {
        return calculator.apply(value, vatRate);
    }

}
