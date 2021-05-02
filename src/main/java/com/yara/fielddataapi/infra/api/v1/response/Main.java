package com.yara.fielddataapi.infra.api.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Main(@JsonProperty("temp") Double temp,
                   @JsonProperty("pressure") Integer pressure,
                   @JsonProperty("humidity") Integer humidity,
                   @JsonProperty("temp_min") Double temp_min,
                   @JsonProperty("temp_max") Double temp_max) {
}
