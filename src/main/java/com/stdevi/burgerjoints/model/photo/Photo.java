package com.stdevi.burgerjoints.model.photo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@ToString
public class Photo {

    @JsonProperty("createdAt")
    private long createdAt;

    @JsonProperty("prefix")
    private String prefix;

    @JsonProperty("suffix")
    private String suffix;

    public String getUrl(String width, String height) {
        return String.format("%s%sx%s%s", prefix, width, height, suffix);
    }

}
