package com.globalblue.homework.api;

import com.globalblue.homework.model.VATNetGrossDistribution;
import com.globalblue.homework.model.GetVATNetGrossDistributionRequestDTO;
import com.globalblue.homework.usecase.CalculateVATNetGrossDistributionUseCase;
import com.globalblue.homework.usecase.exception.InvalidVATRateException;
import com.globalblue.homework.usecase.exception.MutuallyExclusiveParametersException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@RestController()
@RequestMapping(VATNetGrossDistributionController.REQUEST_MAPPING)
@AllArgsConstructor
@Validated
public class VATNetGrossDistributionController {

    public static final String REQUEST_MAPPING = "vat-net-gross-distribution";

    private final CalculateVATNetGrossDistributionUseCase calculateVATNetGrossDistributionUseCase;

    @GetMapping("/{vatRate}")
    public VATNetGrossDistribution getVATNetGrossDistribution(
            @PathVariable @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = Integer.MAX_VALUE, fraction = 0) BigDecimal vatRate,
            @RequestParam(name = "net", required = false) @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = Integer.MAX_VALUE, fraction = 2) BigDecimal netAmount,
            @RequestParam(name = "gross", required = false) @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = Integer.MAX_VALUE, fraction = 2) BigDecimal grossAmount,
            @RequestParam(name = "vat", required = false) @DecimalMin(value = "0.0", inclusive = false) @Digits(integer = Integer.MAX_VALUE, fraction = 2) BigDecimal vatAmount) {
        return calculateVATNetGrossDistributionUseCase.apply
                (GetVATNetGrossDistributionRequestDTO.builder()
                        .vatRate(vatRate.divide(new BigDecimal("100"), 2, RoundingMode.HALF_DOWN))
                        .netAmount(Optional.ofNullable(netAmount))
                        .grossAmount(Optional.ofNullable(grossAmount))
                        .vatAmount(Optional.ofNullable(vatAmount))
                        .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("Not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidVATRateException.class)
    ResponseEntity<String> handleInvalidVATRateException() {
        return new ResponseEntity<>("Invalid VAT rate", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MutuallyExclusiveParametersException.class)
    ResponseEntity<String> handleMutuallyExclusiveParametersException() {
        return new ResponseEntity<>("Net, gross and vat parameters are mutually exclusive, exactly one of them should be provided", HttpStatus.BAD_REQUEST);
    }
}
