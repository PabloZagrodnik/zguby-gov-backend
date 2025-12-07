package pl.hacknation.backend.Service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.hacknation.backend.Entity.FoundItem;
import pl.hacknation.backend.Entity.ItemCategory;
import pl.hacknation.backend.Entity.ItemStatus;
import pl.hacknation.backend.Repository.FoundItemRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ChatModel chatModel;
    private final FoundItemRepository repository;
    private final ObjectMapper objectMapper;

    // Folder do zapisu zdjęć
    private final Path uploadDir = Paths.get("uploads");

    public FoundItem analyzeImage(MultipartFile file) throws IOException {
        var imageResource = new ByteArrayResource(file.getBytes());

        String promptText = """
            Przeanalizuj to zdjęcie zgubionego przedmiotu.
            Zwróć TYLKO czysty JSON (bez markdown).
            Dopasuj do struktury:
            {
               "title": "krótki tytuł np. Czerwony portfel",
               "category": "jeden z: electronics, documents, keys, clothing, accessories, bags, jewelry, bicycles, cash, other",
               "description": "szczegółowy opis przedmiotu",
               "color": "dominujący kolor",
               "brand": "marka jeśli widoczna, inaczej null"
            }
            """;

        var userMessage = new UserMessage(
                promptText,
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG, imageResource))
        );

        String aiResponse = chatModel.call(new Prompt(userMessage)).getResult().getOutput().getText();
        String cleanJson = aiResponse.replace("```json", "").replace("```", "").trim();

        // Mapowanie odpowiedzi AI do tymczasowego DTO
        AiAnalysisResponse aiData = objectMapper.readValue(cleanJson, AiAnalysisResponse.class);

        // Zapis pliku fizycznie i generowanie URL
        String imageUrl = saveFileAndGetUrl(file);

        // Budowanie encji FoundItem
        FoundItem item = new FoundItem();
        item.setRegistryNumber("REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        item.setTitle(aiData.title());
        item.setCategory(aiData.category());
        item.setDescription(aiData.description());
        item.setColor(aiData.color());
        item.setBrand(aiData.brand());
        item.setImageUrl(imageUrl);
        item.setStatus(ItemStatus.AVAILABLE);


        return item;
    }

    public FoundItem saveItem(FoundItem item) {
        if (item.getEventDate() == null) {
            item.setEventDate(LocalDateTime.now());
        }
        if (item.getLocation() == null || item.getLocation().isEmpty()) {
            item.setLocation("Biuro Główne");
        }
        // Upewniamy się, że status jest ustawiony
        if (item.getStatus() == null) {
            item.setStatus(ItemStatus.AVAILABLE);
        }

        return repository.save(item);
    }

    private String saveFileAndGetUrl(MultipartFile file) throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation);


        return "/uploads/" + filename;
    }

    // Rekord do parsowania
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record AiAnalysisResponse(
            String title,
            ItemCategory category,
            String description,
            String color,
            String brand

    ) {}
}