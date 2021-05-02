package com.yara.fielddataapi.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Weather(@JsonProperty("timestamp") String timestamp,
                      @JsonProperty("temperature") Double temperature,
                      @JsonProperty("humidity") Integer humidity,
                      @JsonProperty("temperatureMax") Double temperatureMax,
                      @JsonProperty("temperatureMin") Double temperatureMin) {
}
