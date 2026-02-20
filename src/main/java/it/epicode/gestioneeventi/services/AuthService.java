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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JWTTools jwtTools;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    public UserResponseDTO register(RegisterDTO dto) {
        if (usersRepository.findByUsername(dto.username()).isPresent()) {
            throw new BadRequestException("Username gia' in uso");
        }

        if (usersRepository.findByEmail(dto.email()).isPresent()) {
            throw new BadRequestException("Email gia' in uso");
        }

        // imposta role di default USER se non fornito
        Role role = dto.role() != null ? dto.role() : Role.USER;

        User newUser = new User(
                dto.username(),
                dto.email(),
                passwordEncoder.encode(dto.password()),
                role
        );

        User savedUser = usersRepository.save(newUser);

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    public LoginResponseDTO login(LoginDTO dto) {
        User user = usersRepository.findByUsernameOrEmail(dto.emailOrUsername(), dto.emailOrUsername())
                .orElseThrow(() -> new UnauthorizedException("Credenziali non valide"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new UnauthorizedException("Credenziali non valide");
        }

        String token = jwtTools.generateToken(user.getId());

        return new LoginResponseDTO(
                token,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
