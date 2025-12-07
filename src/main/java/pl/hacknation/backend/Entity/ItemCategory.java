package pl.hacknation.backend.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ItemCategory {

    @JsonProperty("documents")
    DOCUMENTS,

    @JsonProperty("electronics")
    ELECTRONICS,

    @JsonProperty("clothing")
    CLOTHING,

    @JsonProperty("accessories")
    ACCESSORIES,

    @JsonProperty("keys")
    KEYS,

    @JsonProperty("bags")
    BAGS,

    @JsonProperty("jewelry")
    JEWELRY,

    @JsonProperty("bicycles")
    BICYCLES,

    @JsonProperty("cash")
    CASH,

    @JsonProperty("other")
    OTHER;
}