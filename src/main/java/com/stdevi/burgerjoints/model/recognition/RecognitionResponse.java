package com.stdevi.burgerjoints.model.recognition;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class RecognitionResponse {

    @JsonProperty("urlWithBurger")
    private String urlWithBurger;

    @JsonProperty("error")
    private String error;
}
