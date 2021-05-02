package com.yara.fielddataapi.infra.api.v1.response;

import com.yara.fielddataapi.application.domain.GeoJson;

import java.util.List;

public record PolygonResponse(String id,
                              GeoJson geoJson,
                              String name,
                              List<Double> center,
                              Double area,
                              String user_id) {

}
