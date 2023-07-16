package com.globalblue.homework.api;

import com.globalblue.homework.model.VATNetGrossDistribution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpApiTest {

    @Value(value = "${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testGetVATNetGrossDistribution_withValidInput_expectValidOutput() {
        URI uri = getBaseUriBuilder()
                .queryParam("net", "100")
                .build(10);

        ResponseEntity<VATNetGrossDistribution> actual = restTemplate.getForEntity(uri, VATNetGrossDistribution.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody().getVatRate()).isEqualByComparingTo(new BigDecimal("0.1"));
        assertThat(actual.getBody().getGrossAmount()).isEqualByComparingTo(new BigDecimal("110"));
        assertThat(actual.getBody().getNetAmount()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(actual.getBody().getVatValue()).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    public void testGetVATNetGrossDistribution_withZeroNet_expectBadRequest() {
        URI uri = getBaseUriBuilder()
                .queryParam("net", "0")
                .build(10);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetVATNetGrossDistribution_withZeroGross_expectBadRequest() {
        URI uri = getBaseUriBuilder()
                .queryParam("gross", "0")
                .build(10);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetVATNetGrossDistribution_withZeroVAT_expectBadRequest() {
        URI uri = getBaseUriBuilder()
                .queryParam("vat", "0")
                .build(10);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetVATNetGrossDistribution_withZeroVatRate_expectNotFound() {
        URI uri = getBaseUriBuilder()
                .queryParam("vat", "10")
                .build(0);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testGetVATNetGrossDistribution_withInvalidVatRate_expectNotFound() {
        URI uri = getBaseUriBuilder()
                .queryParam("vat", "10")
                .build(9);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testGetVATNetGrossDistribution_withBothNetAndGross_expectBadRequest() {
        URI uri = getBaseUriBuilder()
                .queryParam("net", "100")
                .queryParam("gross", "110")
                .build(10);

        ResponseEntity<String> actual = restTemplate.getForEntity(uri, String.class);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private UriComponentsBuilder getBaseUriBuilder() {
        return UriComponentsBuilder.fromHttpUrl("http://localhost:" + port)
                .pathSegment(VATNetGrossDistributionController.REQUEST_MAPPING, "{vatRate}");
    }
}
