package pl.hacknation.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.hacknation.backend.DTO.ChatRequest;
import pl.hacknation.backend.DTO.ChatResponse;
import pl.hacknation.backend.Service.ChatService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
// CrossOrigin jest już w WebConfig, ale to nie zaszkodzi
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return chatService.processMessage(request);
    }

    @PostMapping("/start")
    public ChatResponse startChat() {
        return chatService.startConversation();
    }
}