package com.globalblue.homework.usecase;

import com.globalblue.homework.model.VATNetGrossDistribution;
import com.globalblue.homework.model.GetVATNetGrossDistributionRequestDTO;
import com.globalblue.homework.repository.StaticVATRateRepository;
import com.globalblue.homework.repository.VATRateRepository;
import com.globalblue.homework.usecase.exception.InvalidVATRateException;
import com.globalblue.homework.usecase.exception.MutuallyExclusiveParametersException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CalculateVATNetGrossDistributionUseCaseTest {

    @Spy
    private VATRateRepository vatRateRepository = new StaticVATRateRepository();

    @InjectMocks
    private CalculateVATNetGrossDistributionUseCase victim;


    @Test
    public void testApply_withValidVATRateAndNetAmount_expectCorrectGrossAndVatValues() {
        GetVATNetGrossDistributionRequestDTO input = GetVATNetGrossDistributionRequestDTO.builder()
                .vatRate(new BigDecimal("0.13"))
                .vatAmount(Optional.empty())
                .netAmount(Optional.of(new BigDecimal("1515.38")))
                .grossAmount(Optional.empty())
                .build();

        VATNetGrossDistribution actual = victim.apply(input);

        assertVATNetGrossDistribution(actual);
    }

    @Test
    public void testApply_withValidVATRateAndGrossAmount_expectCorrectNetAndVatValues() {
        GetVATNetGrossDistributionRequestDTO input = GetVATNetGrossDistributionRequestDTO.builder()
                .vatRate(new BigDecimal("0.13"))
                .vatAmount(Optional.of(new BigDecimal("197")))
                .netAmount(Optional.empty())
                .grossAmount(Optional.empty())
                .build();

        VATNetGrossDistribution actual = victim.apply(input);

        assertVATNetGrossDistribution(actual);
    }

    @Test
    public void testApply_withValidVATRateAndVatValue_expectCorrectNetAndGrossValues() {
        GetVATNetGrossDistributionRequestDTO input = GetVATNetGrossDistributionRequestDTO.builder()
                .vatRate(new BigDecimal("0.13"))
                .vatAmount(Optional.empty())
                .netAmount(Optional.empty())
                .grossAmount(Optional.of(new BigDecimal("1712.38")))
                .build();

        VATNetGrossDistribution actual = victim.apply(input);

        assertVATNetGrossDistribution(actual);
    }

    @Test
    public void testApply_withInvalidVATRate_expectInvalidVATRateException() {
        GetVATNetGrossDistributionRequestDTO input = GetVATNetGrossDistributionRequestDTO.builder()
                .vatRate(new BigDecimal("0.9"))
                .vatAmount(Optional.empty())
                .netAmount(Optional.empty())
                .grossAmount(Optional.of(new BigDecimal("110")))
                .build();

        assertThatThrownBy(() -> victim.apply(input)).isInstanceOf(InvalidVATRateException.class);
        verify(vatRateRepository).getSupportedVATRates();
    }

    @Test
    public void testApply_withMultipleInput_expectMutuallyExclusiveParametersException() {
        GetVATNetGrossDistributionRequestDTO input = GetVATNetGrossDistributionRequestDTO.builder()
                .vatRate(new BigDecimal("0.13"))
                .vatAmount(Optional.of(new BigDecimal("10")))
                .netAmount(Optional.empty())
                .grossAmount(Optional.of(new BigDecimal("110")))
                .build();

        assertThatThrownBy(() -> victim.apply(input)).isInstanceOf(MutuallyExclusiveParametersException.class);
    }

    private void assertVATNetGrossDistribution(VATNetGrossDistribution actual) {
        assertThat(actual.getVatRate()).isEqualByComparingTo(new BigDecimal("0.13"));
        assertThat(actual.getGrossAmount()).isEqualByComparingTo(new BigDecimal("1712.38"));
        assertThat(actual.getNetAmount()).isEqualByComparingTo(new BigDecimal("1515.38"));
        assertThat(actual.getVatValue()).isEqualByComparingTo(new BigDecimal("197"));
    }
}
