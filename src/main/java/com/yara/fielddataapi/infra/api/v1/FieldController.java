package com.yara.fielddataapi.infra.api.v1;

import com.yara.fielddataapi.application.FieldService;
import com.yara.fielddataapi.application.domain.Field;
import com.yara.fielddataapi.application.domain.Weather;
import com.yara.fielddataapi.infra.api.v1.request.FieldRequest;
import com.yara.fielddataapi.infra.api.v1.response.PolygonResponse;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/v1/fields")
public class FieldController {

    @Autowired
    FieldService fieldService;

    @PostMapping
    @ApiOperation(value = "Create a new Field")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully created a new Field"),
            @ApiResponse(code = 404, message = "The Field you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public Field createField(@RequestBody FieldRequest requestBody) {
        return fieldService.createField(requestBody.name(), requestBody.countryCode(), requestBody.boundaries());
    }

    @PutMapping("/{fieldId}")
    @ApiOperation(value = "Update a Field")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully update a Field"),
            @ApiResponse(code = 404, message = "The Field you were trying to update is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public Field updateField(@RequestBody FieldRequest requestBody, @PathVariable String fieldId) {
        return fieldService.updatedField(fieldId, requestBody.name(), requestBody.countryCode(), requestBody.boundaries());
    }

    @GetMapping("/{fieldId}")
    @ApiOperation(value = "Retrieve specific Field with the supplied field id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved the field"),
            @ApiResponse(code = 404, message = "The Field you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public Field fieldById(@PathVariable String fieldId) {
        return fieldService.findFieldBy(fieldId);
    }

    @GetMapping
    @ApiOperation(value = "View all Fields")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved all fields"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public List<Field> fields(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int pageSize) {
        return fieldService.findFields(page, pageSize);
    }

    @DeleteMapping("/{fieldId}")
    @ApiOperation(value = "Deletes specific Field with the supplied field id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully deletes the specific field"),
            @ApiResponse(code = 404, message = "The field you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public void deleteField(@PathVariable String fieldId) {
        fieldService.deleteField(fieldId);
    }

    @GetMapping("/{fieldId}/weather")
    @ApiOperation(value = "Retrieve weather history for the supplied field id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved weather history "),
            @ApiResponse(code = 404, message = "The weather history for the Field you were trying to reach is not found"),
            @ApiResponse(code = 500, message = "Application failed to process the request")
    })
    public List<Weather> retrieveWeatherHistory(@PathVariable String fieldId) {
        return fieldService.retrieveWeatherHistory(fieldId);
    }

    @PostMapping(value = "/{fieldId}/polygon", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<PolygonResponse> addPolygon(@PathVariable String fieldId,
                                            @RequestParam String appid) {
        return fieldService.addPolygon(fieldId, appid);
    }

}
