package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record ServerMessage(
        @JsonProperty("name") String name,
        @JsonProperty("number") int number,
        @JsonProperty("red") double red,
        @JsonProperty("green") double green,
        @JsonProperty("blue") double blue,
        @JsonProperty("opacity") double opacity,
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("status") String status,
        @JsonProperty("account") String account) {
}
