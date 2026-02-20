package it.epicode.gestioneeventi.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record EventUpdateDTO(
        String title,

        String description,

        @Future(message = "Event date must be in the future")
        LocalDateTime event_date,

        String location,

        @Min(value = 1, message = "Available seats must be at least 1")
        Integer available_seats
) {
}
