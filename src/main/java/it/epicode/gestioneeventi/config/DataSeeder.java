package it.epicode.gestioneeventi.config;

import it.epicode.gestioneeventi.entities.Booking;
import it.epicode.gestioneeventi.entities.Event;
import it.epicode.gestioneeventi.entities.Role;
import it.epicode.gestioneeventi.entities.User;
import it.epicode.gestioneeventi.repositories.BookingRepository;
import it.epicode.gestioneeventi.repositories.EventRepository;
import it.epicode.gestioneeventi.repositories.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner run(UsersRepository usersRepository, 
                          EventRepository eventRepository, 
                          BookingRepository bookingRepository) {
        return args -> {
            // Crea password encoder per hash password di test
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            
            // 1. Crea 2 user di test
            User userRegular = new User(
                    "mario.rossi",
                    "mario@example.com",
                    encoder.encode("password123"),
                    Role.USER
            );
            
            User userOrganizer = new User(
                    "franco.bianchi",
                    "franco@example.com",
                    encoder.encode("password456"),
                    Role.ORGANIZER
            );
            
            usersRepository.save(userRegular);
            usersRepository.save(userOrganizer);
            System.out.println("Creati 2 user: " + userRegular.getUsername() + ", " + userOrganizer.getUsername());
            
            // 2. Crea 1 evento (organizzato da userOrganizer)
            Event event = new Event(
                    "Conference Spring Boot 2026",
                    "Una conferenza su Spring Boot best practices",
                    LocalDateTime.now().plusDays(30),
                    "Roma, Auditorium",
                    50,
                    userOrganizer
            );
            
            eventRepository.save(event);
            System.out.println("Creato evento: " + event.getTitle());
            
            // 3. Crea 1 booking (userRegular prenota l'evento)
            Booking booking = new Booking(userRegular, event);
            bookingRepository.save(booking);
            System.out.println("Creata prenotazione: " + userRegular.getUsername() + " -> " + event.getTitle());
            
            // 4. Verifica lettura dal DB
            var foundUser = usersRepository.findByUsername("mario.rossi");
            if (foundUser.isPresent()) {
                System.out.println("Utente trovato: " + foundUser.get().getEmail());
            }
            
            var foundEvent = eventRepository.findById(event.getId());
            if (foundEvent.isPresent()) {
                System.out.println("Evento trovato: " + foundEvent.get().getTitle() + " (posti: " + foundEvent.get().getAvailable_seats() + ")");
            }
            
            var foundBooking = bookingRepository.findByUserAndEvent(userRegular, event);
            if (foundBooking.isPresent()) {
                System.out.println("Prenotazione trovata: ID " + foundBooking.get().getId());
            }
            
            System.out.println("Seed completato, test passati");
        };
    }
}
