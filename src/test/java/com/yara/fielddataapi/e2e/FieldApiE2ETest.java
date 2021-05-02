package com.yara.fielddataapi.e2e;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yara.fielddataapi.application.FieldRepository;
import com.yara.fielddataapi.application.domain.Field;
import com.yara.fielddataapi.application.domain.Weather;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FieldApiE2ETest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private FieldRepository fieldRepository;

    @Test
    @Order(1)
    public void givenNoFieldsWhenReceivingNewFieldItShouldSaveTheFieldInTheDb() throws JsonProcessingException {
        //when
        String requestBody = readFile("request/field_post.json");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        ResponseEntity<String> exchange = testRestTemplate.exchange(URI.create("/v1/fields"),
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class);

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

        Field expectedField = mapToField(requestBody);
        List<Field> actualFields = fieldRepository.findAll();

        assertThat(actualFields).hasSize(1);

        Field actualField = actualFields.get(0);
        assertThat(actualField.created()).isNotEmpty();
        assertThat(actualField.updated()).isEmpty();
        assertThat(actualField.name()).isEqualTo(expectedField.name());
        assertThat(actualField.countryCode()).isEqualTo(expectedField.countryCode());
        assertThat(actualField.boundaries().created()).isNotEmpty();
        assertThat(actualField.boundaries().updated()).isEmpty();
        assertThat(actualField.boundaries().geoJson()).isEqualTo(expectedField.boundaries().geoJson());
    }

    @Test
    @Order(2)
    public void givenFieldWhenReceivingUpdateFieldItShouldUpdateTheFieldInTheDb() throws JsonProcessingException {
        //given
        List<Field> fields = fieldRepository.findAll();
        assertThat(fields).hasSize(1);
        String fieldId = fields.get(0).id();

        //when
        String requestBody = readFile("request/field_update.json");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        ResponseEntity<String> exchange = testRestTemplate.exchange(URI.create("/v1/fields/" + fieldId),
                HttpMethod.PUT,
                new HttpEntity<>(requestBody, headers),
                String.class);

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

        Field expectedField = mapToField(requestBody);
        List<Field> actualFields = fieldRepository.findAll();

        assertThat(actualFields).hasSize(1);

        Field actualField = actualFields.get(0);
        assertThat(actualField.created()).isNotEmpty();
        assertThat(actualField.updated()).isNotEmpty();
        assertThat(actualField.name()).isEqualTo(expectedField.name());
        assertThat(actualField.countryCode()).isEqualTo(expectedField.countryCode());
        assertThat(actualField.boundaries().created()).isNotEmpty();
        assertThat(actualField.boundaries().updated()).isNotEmpty();
        assertThat(actualField.boundaries().geoJson()).isEqualTo(expectedField.boundaries().geoJson());
    }

    @Test
    @Order(3)
    public void given2FieldsWhenRequestingFieldsItShouldRetrieveAllFieldsFromDb() throws JsonProcessingException {
        //given
        String requestBody = readFile("request/field_post.json");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        ResponseEntity<String> exchange = testRestTemplate.exchange(URI.create("/v1/fields"),
                HttpMethod.POST,
                new HttpEntity<>(requestBody, headers),
                String.class);

        //when
        ResponseEntity<List<Field>> exchange2 = testRestTemplate.exchange(URI.create("/v1/fields"),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<List<Field>>() {
                });

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange2.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Field> actualFields = fieldRepository.findAll();
        assertThat(actualFields).hasSize(2);
    }

    @Test
    @Order(4)
    public void given2FieldsWhenRequestingFieldWhichNotExistsItShouldThrowFieldNotFoundException() throws JsonProcessingException {
        //when
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        ResponseEntity<String> exchange = testRestTemplate.exchange(URI.create("/v1/fields/123"),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                String.class);

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exchange.getBody()).isEqualTo("Requested Field not found");
    }

    @Test
    @Order(5)
    public void givenFieldWhenRequestingWeatherHistoryItShouldRetrieveWeatherList() throws JsonProcessingException {
        //given
        List<Field> fields = fieldRepository.findAll();
        assertThat(fields).hasSize(2);
        String fieldId = fields.get(0).id();

        //when
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        ResponseEntity<List<Weather>> exchange = testRestTemplate.exchange(URI.create("/v1/fields/" + fieldId + "/weather"),
                HttpMethod.GET,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<List<Weather>>() {
                });

        //then
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

        String responseBody = readFile("response/weather_history.json");
        List<Weather> weatherList = mapToList(responseBody, new TypeReference<List<Weather>>(){});
        assertThat(exchange.getBody()).isEqualTo(weatherList);

    }
    private String readFile(String filename) {
        try {
            return FileUtils.readFileToString(new ClassPathResource(filename).getFile(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: "+filename+".", e);
        }
    }

    private Field mapToField(String requestBody) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(requestBody, Field.class);
    }

    private <T> T mapToList(String requestBody, TypeReference<T> type) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(requestBody, type);
    }

}
