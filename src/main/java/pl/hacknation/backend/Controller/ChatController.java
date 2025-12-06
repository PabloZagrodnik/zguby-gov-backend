package pl.hacknation.backend.Controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatClient chatClient;

    // Konfiguracja ChatClienta z Pamięcią i Toolami
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                    Jesteś pomocnym asystentem biura rzeczy znalezionych.
                    Twoim zadaniem jest pomagać ludziom odnaleźć zgubione rzeczy.
                    ZAWSZE używaj dostępnych narzędzi (function calls), aby sprawdzić bazę danych, 
                    zanim odpowiesz, czy przedmiot został znaleziony.
                    Bądź uprzejmy i krótki.
                    """)
                .defaultFunctions("searchLostItems") // Nazwa Beana z ToolsConfig
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .build();
    }

    @PostMapping
    public String chat(@RequestBody String message,
                       @RequestHeader(value = "chatId", defaultValue = "defaultSession") String chatId) {

        return chatClient.prompt()
                .user(message)
                // Pamieć szeregowa
                .advisors(a -> a.param(MessageChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY, chatId))
                .call()
                .content();
    }
}