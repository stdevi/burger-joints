package com.stdevi.burgerjoints.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class Meta {

    private HttpStatus httpStatus;

    @JsonProperty("errorType")
    private String errorType;

    @JsonProperty("errorDetail")
    private String errorDetail;

    @JsonProperty("code")
    private void setHttpStatus(int code) {
        httpStatus = HttpStatus.resolve(code);
    }
}
