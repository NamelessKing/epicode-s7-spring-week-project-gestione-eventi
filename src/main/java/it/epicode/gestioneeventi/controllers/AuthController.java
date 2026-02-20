package it.epicode.gestioneeventi.controllers;

import it.epicode.gestioneeventi.dto.request.LoginDTO;
import it.epicode.gestioneeventi.dto.request.RegisterDTO;
import it.epicode.gestioneeventi.dto.response.LoginResponseDTO;
import it.epicode.gestioneeventi.dto.response.UserResponseDTO;
import it.epicode.gestioneeventi.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints per registrazione e login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Registra un nuovo utente", description = "Crea un nuovo account utente con username, email e password")
    @ApiResponse(responseCode = "201", description = "Utente registrato con successo", content = @Content(schema = @Schema(implementation = UserResponseDTO.class)))
    @ApiResponse(responseCode = "400", description = "Username o email gia' in uso")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
        UserResponseDTO result = authService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    @Operation(summary = "Login utente", description = "Autentica l'utente e ritorna un token JWT")
    @ApiResponse(responseCode = "200", description = "Login riuscito", content = @Content(schema = @Schema(implementation = LoginResponseDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenziali non valide")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        LoginResponseDTO result = authService.login(dto);
        return ResponseEntity.ok(result);
    }
}
