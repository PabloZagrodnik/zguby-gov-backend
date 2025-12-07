package pl.hacknation.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.hacknation.backend.Entity.FoundItem;
import pl.hacknation.backend.Repository.FoundItemRepository;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Pozwolenie dla frontendu
public class ItemController {

    private final FoundItemRepository repository;

    // GET /api/items - Pobranie listy
    @GetMapping
    public ResponseEntity<List<FoundItem>> getAllItems() {
        return ResponseEntity.ok(repository.findAll());
    }

    // GET /api/items/{id} - Pobranie szczegółów
    @GetMapping("/{id}")
    public ResponseEntity<FoundItem> getItemById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}