package it.epicode.gestioneeventi.services;

import it.epicode.gestioneeventi.dto.request.EventCreateDTO;
import it.epicode.gestioneeventi.dto.request.EventUpdateDTO;
import it.epicode.gestioneeventi.dto.response.EventResponseDTO;
import it.epicode.gestioneeventi.entities.Event;
import it.epicode.gestioneeventi.entities.Role;
import it.epicode.gestioneeventi.entities.User;
import it.epicode.gestioneeventi.exceptions.AuthorizationDeniedException;
import it.epicode.gestioneeventi.exceptions.NotFoundException;
import it.epicode.gestioneeventi.repositories.EventRepository;
import it.epicode.gestioneeventi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UsersRepository usersRepository;

    public List<EventResponseDTO> getAll() {
        return eventRepository.findAll().stream()
                .map(this::convertToDTO)
                .toList();
    }

    public EventResponseDTO getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento non trovato"));
        return convertToDTO(event);
    }

    @Transactional
    public EventResponseDTO create(EventCreateDTO dto, Long userId) {
        // Verifica che l'utente esista
        User organizer = usersRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // Verifica che l'utente sia ORGANIZER
        if (organizer.getRole() != Role.ORGANIZER) {
            throw new AuthorizationDeniedException("Solo gli ORGANIZER possono creare eventi");
        }

        // Crea l'evento
        Event event = new Event(
                dto.title(),
                dto.description(),
                dto.event_date(),
                dto.location(),
                dto.available_seats(),
                organizer
        );

        Event savedEvent = eventRepository.save(event);
        return convertToDTO(savedEvent);
    }

    @Transactional
    public EventResponseDTO update(Long id, EventUpdateDTO dto, Long userId) {
        // Carica l'evento
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento non trovato"));

        // Verifica ownership - solo il creatore può modificare
        if (!event.getOrganizer().getId().equals(userId)) {
            throw new AuthorizationDeniedException("Non hai i permessi per modificare questo evento");
        }

        // Aggiorna i campi se forniti
        if (dto.title() != null) {
            event.setTitle(dto.title());
        }
        if (dto.description() != null) {
            event.setDescription(dto.description());
        }
        if (dto.event_date() != null) {
            event.setEvent_date(dto.event_date());
        }
        if (dto.location() != null) {
            event.setLocation(dto.location());
        }
        if (dto.available_seats() != null) {
            event.setAvailable_seats(dto.available_seats());
        }

        Event updatedEvent = eventRepository.save(event);
        return convertToDTO(updatedEvent);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        // Carica l'evento
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento non trovato"));

        // Verifica ownership - solo il creatore può eliminare
        if (!event.getOrganizer().getId().equals(userId)) {
            throw new AuthorizationDeniedException("Non hai i permessi per eliminare questo evento");
        }

        eventRepository.delete(event);
    }

    private EventResponseDTO convertToDTO(Event event) {
        return new EventResponseDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getEvent_date(),
                event.getLocation(),
                event.getAvailable_seats(),
                event.getOrganizer().getId(),
                event.getOrganizer().getUsername()
        );
    }
}
