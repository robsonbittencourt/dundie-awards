package com.ninjaone.dundie_awards.dundie.delivery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static java.util.UUID.fromString;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DundieDeliveryIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCompleteDundieDeliverySuccessfully() throws Exception {
        long organizationId = 1L;

        var response = restTemplate.postForEntity("/give-dundie-awards/" + organizationId, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode json = objectMapper.readTree(response.getBody());
        UUID deliveryId = fromString(json.get("deliveryId").asText());
        assertThat(deliveryId).isNotNull();

        Thread.sleep(5000);

        var statusResponse = restTemplate.getForEntity("/dundie-delivery/" + deliveryId, String.class);
        assertThat(statusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        String status = objectMapper.readTree(statusResponse.getBody()).get("status").asText();
        assertThat(status).isEqualTo("FINISHED");
    }
}