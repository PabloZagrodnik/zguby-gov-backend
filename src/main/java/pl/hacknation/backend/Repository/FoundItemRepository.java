package pl.hacknation.backend.Repository;

import pl.hacknation.backend.Entity.FoundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoundItemRepository extends JpaRepository<FoundItem, Long> {

    // Customowe zapytanie szukające po opisie lub kategorii
    @Query("SELECT f FROM FoundItem f WHERE " +
            "LOWER(f.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.category) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(f.color) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<FoundItem> searchByQuery(String query);
}