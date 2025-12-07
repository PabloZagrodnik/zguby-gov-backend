package pl.hacknation.backend.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TicketStatus {
    @JsonProperty("pending")
    PENDING,

    @JsonProperty("in_progress")
    IN_PROGRESS,

    @JsonProperty("matched")
    MATCHED,

    @JsonProperty("not_found")
    NOT_FOUND,

    @JsonProperty("closed")
    CLOSED
}