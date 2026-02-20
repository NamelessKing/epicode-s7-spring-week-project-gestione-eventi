package it.epicode.gestioneeventi.services;

import it.epicode.gestioneeventi.dto.request.LoginDTO;
import it.epicode.gestioneeventi.dto.request.RegisterDTO;
import it.epicode.gestioneeventi.dto.response.LoginResponseDTO;
import it.epicode.gestioneeventi.dto.response.UserResponseDTO;
import it.epicode.gestioneeventi.entities.Role;
import it.epicode.gestioneeventi.entities.User;
import it.epicode.gestioneeventi.exceptions.BadRequestException;
import it.epicode.gestioneeventi.exceptions.UnauthorizedException;
import it.epicode.gestioneeventi.repositories.UsersRepository;
import it.epicode.gestioneeventi.security.JWTTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private JWTTools jwtTools;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterSuccess() {
        RegisterDTO dto = new RegisterDTO("testuser", "test@example.com", "password123", Role.USER);

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        
        // Mock dell'utente salvato con ID già assegnato dal DB
        User savedUser = new User("testuser", "test@example.com", passwordEncoder.encode("password123"), Role.USER);
        when(usersRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponseDTO result = authService.register(dto);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        assertEquals("USER", result.role());
    }

    @Test
    public void testRegisterDuplicateUsername() {
        RegisterDTO dto = new RegisterDTO("testuser", "test@example.com", "password123", Role.USER);

        User existingUser = new User("testuser", "existing@example.com", "hash", Role.USER);
        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> authService.register(dto));
    }

    @Test
    public void testRegisterDuplicateEmail() {
        RegisterDTO dto = new RegisterDTO("testuser", "test@example.com", "password123", Role.USER);

        when(usersRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        User existingUser = new User("otheruser", "test@example.com", "hash", Role.USER);
        when(usersRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> authService.register(dto));
    }

    @Test
    public void testLoginSuccess() {
        LoginDTO dto = new LoginDTO("testuser", "password123");

        User user = new User("testuser", "test@example.com", passwordEncoder.encode("password123"), Role.USER);
        when(usersRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(user));
        when(jwtTools.generateToken(user.getId())).thenReturn("jwt-token-123");

        LoginResponseDTO result = authService.login(dto);

        assertNotNull(result);
        assertEquals("jwt-token-123", result.token());
        assertEquals("testuser", result.username());
    }

    @Test
    public void testLoginUserNotFound() {
        LoginDTO dto = new LoginDTO("nonexistent", "password123");

        when(usersRepository.findByUsernameOrEmail("nonexistent", "nonexistent")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(dto));
    }

    @Test
    public void testLoginWrongPassword() {
        LoginDTO dto = new LoginDTO("testuser", "wrongpassword");

        User user = new User("testuser", "test@example.com", passwordEncoder.encode("password123"), Role.USER);
        when(usersRepository.findByUsernameOrEmail("testuser", "testuser")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.login(dto));
    }
}
