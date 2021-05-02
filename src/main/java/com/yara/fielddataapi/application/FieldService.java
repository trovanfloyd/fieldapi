package com.yara.fielddataapi.application;

import com.yara.fielddataapi.application.domain.*;
import com.yara.fielddataapi.application.domain.exception.FieldNotFoundException;
import com.yara.fielddataapi.application.domain.exception.InvalidFieldRequestException;
import com.yara.fielddataapi.infra.api.v1.AgroMonitoringClient;
import com.yara.fielddataapi.infra.api.v1.request.PolygonRequest;
import com.yara.fielddataapi.infra.api.v1.response.PolygonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FieldService {

    @Autowired
    FieldRepository fieldRepository;

    @Autowired
    AgroMonitoringClient agroMonitoringClient;

    public Field createField(String name, String countryCode, Boundaries boundaries) {
        if (isValid(name, countryCode)) {
            String id = Field.createId();
            String createdAt = Instant.now().toString();
            Boundaries newBoundary = new Boundaries(id, createdAt, "", boundaries.geoJson());
            Field newField = new Field(id, name, createdAt, "", countryCode, newBoundary);

            log.info("Saving new Field: {}", newField);
            return fieldRepository.save(newField);
        } else {
            throw new InvalidFieldRequestException("name/countryCode cannot be null or empty");
        }
    }

    public Field updatedField(String fieldId, String name, String countryCode, Boundaries boundaries) {
        if (isValid(fieldId, name, countryCode)) {
            Optional<Field> field = fieldRepository.findById(fieldId);
            if (field.isPresent()) {
                String updatedAt = Instant.now().toString();
                Boundaries newBoundary = new Boundaries(field.get().boundaries().id(), field.get().boundaries().created(), updatedAt, boundaries.geoJson());
                Field updateField = new Field(fieldId, name, field.get().created(), updatedAt, countryCode, newBoundary);

                log.info("Updating Field: {}", updateField);
                return fieldRepository.save(updateField);
            } else {
                throw new FieldNotFoundException("Field Not Found with id: " + fieldId);
            }
        } else {
            throw new InvalidFieldRequestException("fieldId/name/countryCode cannot be null or empty");
        }
    }

    public Field findFieldBy(String fieldId) {
        if (isValid(fieldId)) {
            Optional<Field> field = fieldRepository.findById(fieldId);
            if (field.isPresent()) {
                return field.get();
            } else {
                throw new FieldNotFoundException("Field Not Found with id: " + fieldId);
            }
        }

        return Field.UNKNOWN_FIELD;
    }

    public List<Field> findFields(int page, int pageSize) {
        Page<Field> fields = fieldRepository.findAll(PageRequest.of(page, pageSize));
        return fields.get().collect(Collectors.toList());
    }

    public void deleteField(String fieldId) {
        if (isValid(fieldId)) {
            fieldRepository.findById(fieldId).ifPresentOrElse(field -> fieldRepository.delete(field), () -> {
                throw new FieldNotFoundException("Field Not Found with id: " + fieldId);
            });
        } else {
            throw new InvalidFieldRequestException("fieldId cannot be null or empty");
        }
    }

    public List<Weather> retrieveWeatherHistory(String fieldId) {
        if (isValid(fieldId)) {
            List<Weather> weatherList = new ArrayList<>();
            fieldRepository.findById(fieldId).ifPresentOrElse(field -> {
                agroMonitoringClient.getWeatherHistory().block().forEach( weatherHistoryResponse -> {
                    weatherList.add(new Weather(weatherHistoryResponse.dt().toString(), weatherHistoryResponse.main().temp(),weatherHistoryResponse.main().humidity(), weatherHistoryResponse.main().temp_min(), weatherHistoryResponse.main().temp_max()));
                });
            }, () -> {
                throw new FieldNotFoundException("Field Not Found with id: " + fieldId);
            });
            return weatherList;
        } else {
            throw new InvalidFieldRequestException("fieldId cannot be null or empty");
        }

    }

    public Mono<PolygonResponse> addPolygon(String fieldId, String appid) {
        if (isValid(fieldId, appid)) {
            Optional<Field> field = fieldRepository.findById(fieldId);
            if (field.isPresent()) {
                Field fieldReq = field.get();
                PolygonRequest polygonRequest = new PolygonRequest(fieldReq.name(), fieldReq.boundaries().geoJson());
                return agroMonitoringClient.createPolygon(polygonRequest);
            } else {
                throw new FieldNotFoundException("Field Not Found with id: " + fieldId);
            }
        } else {
            throw new InvalidFieldRequestException("fieldId/appid cannot be null or empty");
        }
    }

    private boolean isValid(String... values) {
        for(String value: values) {
            if (null == value || value.isBlank()) {
                return false;
            }
        }
        return true;
    }
}
