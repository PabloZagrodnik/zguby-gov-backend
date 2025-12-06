package pl.hacknation.backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import pl.hacknation.backend.Entity.FoundItem;
import pl.hacknation.backend.Repository.FoundItemRepository;

import java.util.List;
import java.util.function.Function;

@Configuration
public class ToolsConfig {

    public record SearchRequest(String query) {}

    public record ItemDto(String category, String description, String color, String location) {}

    @Bean
    @Description("Przeszukuje bazę rzeczy znalezionych na podstawie opisu, koloru lub kategorii.")
    public Function<SearchRequest, List<ItemDto>> searchLostItems(FoundItemRepository repository) {
        return request -> {
            System.out.println(">>> AI CALLING TOOL: Szukam frazy '" + request.query() + "'");

            List<FoundItem> items = repository.searchByQuery(request.query());

            return items.stream()
                    .map(item -> new ItemDto(
                            item.getCategory(),
                            item.getDescription(),
                            item.getColor(),
                            item.getLocation()))
                    .toList();
        };
    }
}