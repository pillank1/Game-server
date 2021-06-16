package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AuthInfo(
        @JsonProperty("name") String name,
        @JsonProperty("red") double red,
        @JsonProperty("green") double green,
        @JsonProperty("blue") double blue,
        @JsonProperty("opacity") double opacity) {
}
