package pl.hacknation.backend.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel; // ZMIANA IMPORTU
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import pl.hacknation.backend.DTO.ChatRequest;
import pl.hacknation.backend.DTO.ChatResponse;
import pl.hacknation.backend.Entity.Ticket;
import pl.hacknation.backend.Entity.TicketStatus;
import pl.hacknation.backend.Repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private final ChatModel chatModel; // ZMIANA: ChatModel jest dostępny z automatu
    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_MESSAGES = 10;

    public ChatResponse processMessage(ChatRequest request) {
        int historySize = request.history() != null ? request.history().size() : 0;

        if (historySize >= MAX_MESSAGES) {
            return createTicketFromHistory(request);
        }

        try {
            String systemText = """
                Jesteś urzędnikiem biura rzeczy znalezionych.
                Twój cel: Zidentyfikować zgubiony przedmiot.
                Zadawaj PO JEDNYM pytaniu na raz.
                Nie generuj jeszcze zgłoszenia, tylko zbieraj informacje.
                """;

            List<Message> messages = new ArrayList<>();
            messages.add(new SystemMessage(systemText));

            // Tutaj można dodać historię z request.history(), jeśli frontend ją przesyła

            messages.add(new UserMessage(request.message()));

            // Wywołanie ChatModel (zamiast ChatClient)
            String aiResponse = chatModel.call(new Prompt(messages))
                    .getResult().getOutput().getText();

            return new ChatResponse(aiResponse, false, null);

        } catch (Exception e) {
            logger.error("Błąd AI (Chat): " + e.getMessage());
            return new ChatResponse("Przepraszam, wystąpił błąd systemu AI. Spróbuj ponownie później.", false, null);
        }
    }

    public ChatResponse startConversation() {
        return new ChatResponse("Dzień dobry! Jestem wirtualnym asystentem. Opisz, co zgubiłeś, a sprawdzę naszą bazę.", false, null);
    }

    private ChatResponse createTicketFromHistory(ChatRequest request) {
        TicketDraft draft;
        try {
            String summaryPrompt = """
                Przeanalizuj rozmowę i wyciągnij dane do zgłoszenia zgubienia.
                Zwróć TYLKO czysty JSON w formacie:
                {
                  "itemType": "...",
                  "color": "...",
                  "location": "...",
                  "eventDate": "...",
                  "features": "...",
                  "contactEmail": "..."
                }
                Ostatnia wiadomość: %s
                """.formatted(request.message());

            // Wywołanie ChatModel
            String jsonResponse = chatModel.call(new Prompt(summaryPrompt))
                    .getResult().getOutput().getText();

            String cleanJson = jsonResponse.replace("```json", "").replace("```", "").trim();
            draft = objectMapper.readValue(cleanJson, TicketDraft.class);
        } catch (Exception e) {
            logger.error("Błąd AI (Ticket): " + e.getMessage());
            draft = new TicketDraft("Przedmiot (Błąd)", "-", "-", "-", "Wypełnij ręcznie", "");
        }

        Ticket ticket = new Ticket();
        ticket.setTicketNumber("ZG-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        ticket.setItemType(draft.itemType != null ? draft.itemType : "Nieznany");
        ticket.setColor(draft.color);
        ticket.setLocation(draft.location);
        ticket.setEventDate(draft.eventDate);
        ticket.setFeatures(draft.features);
        ticket.setContactEmail(draft.contactEmail);
        ticket.setStatus(TicketStatus.PENDING);

        ticketRepository.save(ticket);

        return new ChatResponse(
                "Dziękuję. Przyjąłem zgłoszenie.",
                true,
                mapTicketToFrontendFormat(ticket)
        );
    }

    // Klasy pomocnicze
    private record TicketDraft(String itemType, String color, String location, String eventDate, String features, String contactEmail) {}

    private Object mapTicketToFrontendFormat(Ticket t) {
        return Map.of(
                "id", t.getId().toString(),
                "ticketNumber", t.getTicketNumber(),
                "status", "pending",
                "report", Map.of(
                        "itemType", t.getItemType() != null ? t.getItemType() : "",
                        "color", t.getColor() != null ? t.getColor() : "",
                        "location", t.getLocation() != null ? t.getLocation() : ""
                )
        );
    }
}