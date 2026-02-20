package it.epicode.gestioneeventi.dto.response;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        String role
) {
}
