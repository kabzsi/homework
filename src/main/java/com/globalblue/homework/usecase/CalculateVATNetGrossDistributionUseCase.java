package com.globalblue.homework.usecase;

import com.globalblue.homework.model.VATNetGrossDistribution;
import com.globalblue.homework.model.GetVATNetGrossDistributionRequestDTO;
import com.globalblue.homework.repository.VATRateRepository;
import com.globalblue.homework.usecase.calculator.VATNetGrossDistributionCalculator;
import com.globalblue.homework.usecase.exception.InvalidVATRateException;
import com.globalblue.homework.usecase.exception.MutuallyExclusiveParametersException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class CalculateVATNetGrossDistributionUseCase implements Function<GetVATNetGrossDistributionRequestDTO, VATNetGrossDistribution> {

    private final VATRateRepository vatRateRepository;

    @Override
    public VATNetGrossDistribution apply(GetVATNetGrossDistributionRequestDTO getVatNetGrossDistributionRequestDTO) {
        checkVATRate(getVatNetGrossDistributionRequestDTO);
        checkInputParameters(getVatNetGrossDistributionRequestDTO);

        VATNetGrossDistributionCalculator calculator = VATNetGrossDistributionCalculator.getCalculator(getVatNetGrossDistributionRequestDTO.getVatRate(), getVatNetGrossDistributionRequestDTO.getProvidedValue());
        return calculator.apply(getVatNetGrossDistributionRequestDTO.getValue());
    }

    private void checkInputParameters(GetVATNetGrossDistributionRequestDTO getVatNetGrossDistributionRequestDTO) {
        if (Stream.of(getVatNetGrossDistributionRequestDTO.getGrossAmount(), getVatNetGrossDistributionRequestDTO.getNetAmount(), getVatNetGrossDistributionRequestDTO.getVatAmount())
                .filter(Optional::isPresent).count() != 1) {
            throw new MutuallyExclusiveParametersException();
        }
    }

    private void checkVATRate(GetVATNetGrossDistributionRequestDTO getVatNetGrossDistributionRequestDTO) {
        vatRateRepository.getSupportedVATRates().stream()
                .filter(vatRate -> getVatNetGrossDistributionRequestDTO.getVatRate().compareTo(vatRate) == 0)
                .findAny().orElseThrow(InvalidVATRateException::new);
    }
}
