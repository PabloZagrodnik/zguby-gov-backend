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
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", allowCredentials = "true")
public class AdminController {

    private final AdminService adminService;

    // To obsługuje przycisk "Analizuj zdjęcia" na frontendzie
    @PostMapping("/analyze")
    public ResponseEntity<FoundItem> analyzeItem(@RequestParam("file") MultipartFile file) {
        try {
            FoundItem analyzedItem = adminService.analyzeImage(file);
            return ResponseEntity.ok(analyzedItem);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // To obsługuje przycisk "Zapisz do bazy"
    @PostMapping("/save")
    public ResponseEntity<FoundItem> saveItem(@RequestBody FoundItem item) {
        FoundItem savedItem = adminService.saveItem(item);
        return ResponseEntity.ok(savedItem);
    }
}