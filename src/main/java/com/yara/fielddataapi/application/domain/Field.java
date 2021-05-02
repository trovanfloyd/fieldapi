package com.yara.fielddataapi.application.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("field")
public record Field(@JsonProperty("id") @Id String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("created") String created,
                    @JsonProperty("updated") String updated,
                    @JsonProperty("countryCode") String countryCode,
                    @JsonProperty("boundaries") Boundaries boundaries) {

   public static final Field UNKNOWN_FIELD = new Field("", "", "", "", "", null);

   public static String createId() {
       return UUID.randomUUID().toString();
   }
}
