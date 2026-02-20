package it.epicode.gestioneeventi.dto.response;

import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        String title,
        String description,
        LocalDateTime event_date,
        String location,
        Integer available_seats,
        Long organizer_id,
        String organizer_username
) {
}
