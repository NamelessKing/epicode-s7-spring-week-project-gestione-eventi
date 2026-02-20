package it.epicode.gestioneeventi.repositories;

import it.epicode.gestioneeventi.entities.Booking;
import it.epicode.gestioneeventi.entities.Event;
import it.epicode.gestioneeventi.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByUserAndEvent(User user, Event event);
    List<Booking> findByUser(User user);
    boolean existsByUserAndEvent(User user, Event event);
}
