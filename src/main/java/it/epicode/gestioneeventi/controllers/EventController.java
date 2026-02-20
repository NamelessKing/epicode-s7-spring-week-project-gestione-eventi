package it.epicode.gestioneeventi.controllers;

import it.epicode.gestioneeventi.dto.request.EventCreateDTO;
import it.epicode.gestioneeventi.dto.request.EventUpdateDTO;
import it.epicode.gestioneeventi.dto.response.EventResponseDTO;
import it.epicode.gestioneeventi.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import it.epicode.gestioneeventi.entities.User;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Endpoints per la gestione degli eventi")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    @Operation(summary = "Lista tutti gli eventi", description = "Ritorna la lista di tutti gli eventi (pubblico)")
    @ApiResponse(responseCode = "200", description = "Lista di eventi", content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    public ResponseEntity<List<EventResponseDTO>> getAll() {
        List<EventResponseDTO> events = eventService.getAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ottieni un evento", description = "Ritorna un evento per ID (pubblico)")
    @ApiResponse(responseCode = "200", description = "Evento trovato", content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    @ApiResponse(responseCode = "404", description = "Evento non trovato")
    public ResponseEntity<EventResponseDTO> getById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ORGANIZER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Crea un nuovo evento", description = "Crea un nuovo evento (solo ORGANIZER)")
    @ApiResponse(responseCode = "201", description = "Evento creato", content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Non sei un ORGANIZER")
    @ApiResponse(responseCode = "400", description = "Dati non validi")
    public ResponseEntity<EventResponseDTO> create(@Valid @RequestBody EventCreateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        EventResponseDTO event = eventService.create(dto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Aggiorna un evento", description = "Aggiorna un evento (solo ORGANIZER e proprietario)")
    @ApiResponse(responseCode = "200", description = "Evento aggiornato", content = @Content(schema = @Schema(implementation = EventResponseDTO.class)))
    @ApiResponse(responseCode = "403", description = "Non sei il proprietario dell'evento")
    @ApiResponse(responseCode = "404", description = "Evento non trovato")
    public ResponseEntity<EventResponseDTO> update(@PathVariable Long id, @Valid @RequestBody EventUpdateDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        EventResponseDTO event = eventService.update(id, dto, user.getId());
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Elimina un evento", description = "Elimina un evento (solo ORGANIZER e proprietario)")
    @ApiResponse(responseCode = "204", description = "Evento eliminato")
    @ApiResponse(responseCode = "403", description = "Non sei il proprietario dell'evento")
    @ApiResponse(responseCode = "404", description = "Evento non trovato")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        eventService.delete(id, user.getId());
        return ResponseEntity.noContent().build();
    }
}
