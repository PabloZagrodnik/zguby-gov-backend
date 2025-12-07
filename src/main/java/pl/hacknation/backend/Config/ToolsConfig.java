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

    public record ItemDto(
            String registryNumber,
            String title,
            String category,
            String description,
            String color,
            String brand,
            String location,
            String eventDate
    ) {}

    @Bean
    @Description("Przeszukuje bazę rzeczy znalezionych na podstawie opisu, koloru lub kategorii.")
    public Function<SearchRequest, List<ItemDto>> searchLostItems(FoundItemRepository repository) {
        return request -> {
            List<FoundItem> items = repository.searchByQuery(request.query());

            return items.stream()
                    .map(item -> new ItemDto(
                            item.getRegistryNumber(),
                            item.getTitle(),
                            item.getCategory() != null ? item.getCategory().name() : null,
                            item.getDescription(),
                            item.getColor(),
                            item.getBrand(),
                            item.getLocation(),
                            item.getEventDate() != null ? item.getEventDate().toString() : null
                    ))
                    .toList();
        };
    }
}