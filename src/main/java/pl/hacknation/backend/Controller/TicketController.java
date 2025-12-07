package pl.hacknation.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hacknation.backend.Entity.Ticket;
import pl.hacknation.backend.Entity.TicketStatus;
import pl.hacknation.backend.Repository.TicketRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class TicketController {

    private final TicketRepository ticketRepository;

    // GET /api/tickets - Lista wszystkich zgłoszeń
    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();

        List<TicketDto> dtos = tickets.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // PUT /api/tickets/{id}/status - Aktualizacja statusu i notatek
    @PutMapping("/{id}/status")
    public ResponseEntity<TicketDto> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {

        return ticketRepository.findById(id)
                .map(ticket -> {
                    if (request.status() != null) {
                        ticket.setStatus(request.status());
                    }
                    if (request.notes() != null) {
                        ticket.setNotes(request.notes());
                    }
                    Ticket saved = ticketRepository.save(ticket);
                    return ResponseEntity.ok(mapToDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // --- DTO i Mapowanie ---

    // Konwertuje encję bazodanową na strukturę JSON dla frontendu
    private TicketDto mapToDto(Ticket t) {
        return new TicketDto(
                t.getId().toString(),
                t.getTicketNumber(),
                t.getStatus(),
                t.getCreatedAt() != null ? t.getCreatedAt().toString() : "",
                t.getUpdatedAt() != null ? t.getUpdatedAt().toString() : null,
                t.getNotes(),
                new ReportDto(
                        t.getItemType(),
                        t.getColor(),
                        t.getLocation(),
                        t.getEventDate(),
                        t.getFeatures(),
                        t.getContactEmail(),
                        null // telefon opcjonalny
                )
        );
    }

    // Struktura JSON zgodna z Twoim Frontend Ticket interface
    record TicketDto(
            String id,
            String ticketNumber,
            TicketStatus status,
            String createdAt,
            String updatedAt,
            String notes,
            ReportDto report
    ) {}

    record ReportDto(
            String itemType,
            String color,
            String location,
            String date,
            String features,
            String contactEmail,
            String contactPhone
    ) {}

    record UpdateStatusRequest(TicketStatus status, String notes) {}
}