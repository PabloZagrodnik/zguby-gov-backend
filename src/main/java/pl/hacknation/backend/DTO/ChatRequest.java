package pl.hacknation.backend.DTO;

import java.util.List;

public record ChatRequest(
        String message,
        List<Object> history // Backend AI i tak ma własną pamięć, ale odbieramy to pole, żeby nie było błędu 400
) {}