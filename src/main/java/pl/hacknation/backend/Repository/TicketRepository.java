package pl.hacknation.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.hacknation.backend.Entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
}