package it.epicode.gestioneeventi.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record EventCreateDTO(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @Future(message = "Event date must be in the future")
        LocalDateTime event_date,

        @NotBlank(message = "Location is required")
        String location,

        @Min(value = 1, message = "Available seats must be at least 1")
        Integer available_seats
) {
}
