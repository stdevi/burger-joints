package com.stdevi.burgerjoints.model.venue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@ToString
public class Venue {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    private String burgerPhotoUrl;
}
