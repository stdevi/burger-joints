package com.stdevi.burgerjoints.model.photo;

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
public class PhotoResponse {

    @JsonProperty("meta")
    private Meta meta;

    @JsonProperty("response")
    private PhotosWrapper photosWrapper;
}
