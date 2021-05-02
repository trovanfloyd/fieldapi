package com.yara.fielddataapi.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Boundaries (@JsonProperty("id") String id,
                          @JsonProperty("created") String created,
                          @JsonProperty("updated") String updated,
                          @JsonProperty("geoJson") GeoJson geoJson) {

}
