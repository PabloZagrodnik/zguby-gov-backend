package pl.hacknation.backend.DTO;

public record ChatResponse(
        String message,
        boolean isComplete,
        Object ticket // <--- To pole było brakujące, a jest wymagane przez kod, który napisałem wcześniej
) {}