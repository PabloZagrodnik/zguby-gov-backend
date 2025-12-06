package pl.hacknation.backend.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.hacknation.backend.Entity.FoundItem;
import pl.hacknation.backend.Service.AdminService;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    // Odbieranie JSON wstępnie wypełniony przez AI
    @PostMapping("/analyze")
    public ResponseEntity<FoundItem> analyzeItem(@RequestParam("file") MultipartFile file) throws IOException {
        FoundItem analyzedItem = adminService.analyzeImage(file);
        return ResponseEntity.ok(analyzedItem);
    }

    // Wysyłanie JSON (poprawiony przez urzędnika + lokalizacja) -> Zapisz w bazie
    @PostMapping("/save")
    public ResponseEntity<FoundItem> saveItem(@RequestBody FoundItem item) {
        FoundItem savedItem = adminService.saveItem(item);
        return ResponseEntity.ok(savedItem);
    }
}