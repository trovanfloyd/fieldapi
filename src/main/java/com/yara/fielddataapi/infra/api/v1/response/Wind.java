package com.yara.fielddataapi.infra.api.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Wind(@JsonProperty("speed") Integer speed,
                   @JsonProperty("deg") Integer deg) {
}
