package it.epicode.gestioneeventi.dto.response;

import java.time.LocalDateTime;

public record ErrorsDTO(
        String message,
        LocalDateTime timestamp
) {
}
