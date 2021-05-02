package com.yara.fielddataapi.infra.api.v1.request;

import com.yara.fielddataapi.application.domain.GeoJson;

public record PolygonRequest(String name,
                             GeoJson geoJson) {
}
