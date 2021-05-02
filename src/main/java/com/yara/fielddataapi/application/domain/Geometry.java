package com.yara.fielddataapi.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Geometry (@JsonProperty("type") String type,
                        @JsonProperty("coordinates") List<List<List<Double>>> coordinates) {
}
