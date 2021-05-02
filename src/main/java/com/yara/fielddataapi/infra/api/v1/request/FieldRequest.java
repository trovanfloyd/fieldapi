package com.yara.fielddataapi.infra.api.v1.request;


import com.yara.fielddataapi.application.domain.Boundaries;

public record FieldRequest(String name,
                           String countryCode,
                           Boundaries boundaries) {}
