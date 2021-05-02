package com.yara.fielddataapi.infra.api.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Clouds(@JsonProperty("all") Integer all) {
}
