package pl.hacknation.backend.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ItemStatus {
    @JsonProperty("available")
    AVAILABLE,

    @JsonProperty("claimed")
    CLAIMED,

    @JsonProperty("returned")
    RETURNED
}