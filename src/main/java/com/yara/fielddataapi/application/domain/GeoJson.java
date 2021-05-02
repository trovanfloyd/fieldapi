package com.yara.fielddataapi.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoJson (@JsonProperty("type") String type,
                       @JsonProperty("properties") Properties properties,
                       @JsonProperty("geometry") Geometry geometry) {

}
