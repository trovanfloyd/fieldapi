package com.yara.fielddataapi.infra.api.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record WeatherHistoryResponse (@JsonProperty("dt") Integer dt,
                                      @JsonProperty("weather") List<Weather> weather,
                                      @JsonProperty("main") Main main,
                                      @JsonProperty("wind") Wind wind,
                                      @JsonProperty("clouds") Clouds clouds) {}

