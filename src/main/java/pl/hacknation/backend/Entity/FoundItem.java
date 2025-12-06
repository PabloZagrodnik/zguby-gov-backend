package  pl.hacknation.backend.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;

    @Column(length = 1000)
    private String description;

    private String color;
    private String location;

    private LocalDateTime dateFound;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] image;
}