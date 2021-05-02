package com.yara.fielddataapi.infra.api.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yara.fielddataapi.application.FieldService;
import com.yara.fielddataapi.application.domain.Field;
import com.yara.fielddataapi.application.domain.exception.InvalidFieldRequestException;
import com.yara.fielddataapi.application.domain.Weather;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FieldControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    FieldService fieldService;

    @BeforeEach
    public void setup() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;
    }


    @Test
    void createField() throws JsonProcessingException {
        String responseBody = readFile("response/field_post.json");
        Field field = mapToField(responseBody);

        Mockito.when(fieldService.createField(any(), any(), any())).thenReturn(field);

        String requestBody = readFile("request/field_post.json");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/v1/fields")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(responseBody));

        Field fieldRequest = mapToField(requestBody);
        Mockito.verify(fieldService).createField(fieldRequest.name(), fieldRequest.countryCode(), fieldRequest.boundaries());
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void updateField() throws JsonProcessingException {
        String responseBody = readFile("response/field_updated.json");
        Field field = mapToField(responseBody);

        Mockito.when(fieldService.updatedField(any(), any(), any(), any())).thenReturn(field);

        String requestBody = readFile("request/field_update.json");
        String fieldId = "50ff28de-1e97-4ac4-aaf3-0d3e30bfcb3b";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .put("/v1/fields/" + fieldId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(responseBody));

        Field fieldRequest = mapToField(requestBody);
        Mockito.verify(fieldService).updatedField(fieldId, fieldRequest.name(), fieldRequest.countryCode(), fieldRequest.boundaries());
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void fieldById() throws JsonProcessingException {
        String responseBody = readFile("response/field_get.json");
        Field field = mapToField(responseBody);

        Mockito.when(fieldService.findFieldBy(any())).thenReturn(field);

        String fieldId = "50ff28de-1e97-4ac4-aaf3-0d3e30bfcb3b";

        given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/fields/" + fieldId)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(responseBody));

        Mockito.verify(fieldService).findFieldBy(fieldId);
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void fields() throws JsonProcessingException {
        String responseBody = readFile("response/field_getAll.json");
        List<Field> fields = mapToList(responseBody, new TypeReference<List<Field>>(){});

        Mockito.when(fieldService.findFields(anyInt(), anyInt())).thenReturn(fields);

        given()
                .header("Content-type", "application/json")
                .param("page", 0)
                .param("pageSize", 5)
                .when()
                .get("/v1/fields")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(responseBody));

        Mockito.verify(fieldService).findFields(0, 5);
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void deleteField() throws JsonProcessingException {

        Mockito.doNothing().when(fieldService).deleteField(any());

        String fieldId = "50ff28de-1e97-4ac4-aaf3-0d3e30bfcb3b";

        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/v1/fields/" + fieldId)
                .then()
                .statusCode(200);

        Mockito.verify(fieldService).deleteField(fieldId);
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void getWeatherHistory() throws JsonProcessingException {
        String responseBody = readFile("response/weather_history.json");
        List<Weather> weatherList = mapToList(responseBody, new TypeReference<List<Weather>>(){});

        Mockito.when(fieldService.retrieveWeatherHistory(any())).thenReturn(weatherList);

        String fieldId = "50ff28de-1e97-4ac4-aaf3-0d3e30bfcb3b";

        given()
                .header("Content-type", "application/json")
                .when()
                .get("/v1/fields/" + fieldId + "/weather")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(is(responseBody));

        Mockito.verify(fieldService).retrieveWeatherHistory(fieldId);
        Mockito.verifyNoMoreInteractions(fieldService);
    }

    @Test
    void whenReceivingInvalidFieldApiShouldReturnInvalidFieldRequestException() throws JsonProcessingException {
        Mockito.when(fieldService.createField(any(), any(), any())).thenThrow(new InvalidFieldRequestException("name/countryCode cannot be null or empty"));

        String requestBody = readFile("request/invalid_field.json");

        given()
                .header("Content-type", "application/json")
                .and()
                .body(requestBody)
                .when()
                .post("/v1/fields")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(is("Invalid value in request: name/countryCode cannot be null or empty"));

        Field fieldRequest = mapToField(requestBody);
        Mockito.verify(fieldService).createField(null, fieldRequest.countryCode(), fieldRequest.boundaries());
        Mockito.verifyNoMoreInteractions(fieldService);
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