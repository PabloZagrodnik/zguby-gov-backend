package pl.hacknation.backend.Service;

import pl.hacknation.backend.Entity.FoundItem;
import pl.hacknation.backend.Repository.FoundItemRepository;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ChatModel chatModel;
    private final FoundItemRepository repository;
    private final ObjectMapper objectMapper;

    public FoundItem analyzeImage(MultipartFile file) throws IOException {

        var imageResource = new ByteArrayResource(file.getBytes());

        // Prompt wymuszający JSON
        String promptText = """
            Przeanalizuj to zdjęcie zgubionego przedmiotu.
            Zwróć TYLKO czysty JSON (bez bloków markdown ```json).
            Pola JSON: category (jedno słowo), description (krótkie zdanie), color.
            Nie dodawaj żadnego innego tekstu.
            """;

        var userMessage = new UserMessage(
                promptText,
                List.of(new Media(MimeTypeUtils.IMAGE_JPEG, imageResource))
        );

        String aiResponse = chatModel.call(new Prompt(userMessage)).getResult().getOutput().getText();

        String cleanJson = aiResponse.replace("```json", "").replace("```", "").trim();

        FoundItem tempItem = objectMapper.readValue(cleanJson, FoundItem.class);

        tempItem.setImage(file.getBytes());

        return tempItem;
    }

    public FoundItem saveItem(FoundItem item) {
        item.setDateFound(LocalDateTime.now());
        if (item.getLocation() == null || item.getLocation().isEmpty()) {
            item.setLocation("Biuro Główne");
        }
        return repository.save(item);
    }
}