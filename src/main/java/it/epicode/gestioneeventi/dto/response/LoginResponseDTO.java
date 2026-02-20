package it.epicode.gestioneeventi.dto.response;

public record LoginResponseDTO(
        String token,
        Long userId,
        String username,
        String email,
        String role
) {
}
