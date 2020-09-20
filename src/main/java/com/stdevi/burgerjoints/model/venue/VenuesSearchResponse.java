package com.stdevi.burgerjoints.model.venue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.stdevi.burgerjoints.model.Meta;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class VenuesSearchResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("response")
    private VenuesWrapper venuesWrapper;
}
