package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record ClientMessage(
        @JsonProperty("name") String name,
        @JsonProperty("x") int x,
        @JsonProperty("y") int y,
        @JsonProperty("isCloseRequest") boolean isCloseRequest) {
}
