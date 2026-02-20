package it.epicode.gestioneeventi.security;

import it.epicode.gestioneeventi.entities.User;
import it.epicode.gestioneeventi.repositories.UsersRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTCheckerFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTools jwtTools;

    @Autowired
    private UsersRepository usersRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Estrai il token dall'header Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Valida il token
                jwtTools.verifyToken(token);
                
                // Estrai l'ID utente dal token
                Long userId = jwtTools.extractIdFromToken(token);
                
                // Carica l'utente dal DB
                User user = usersRepository.findById(userId).orElse(null);
                
                if (user != null) {
                    // Crea authentication token e mettilo nel SecurityContext
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ex) {
                // Token invalido - continua senza autenticazione
            }
        }
        
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Salta il filtro per determinati endpoint
        String path = request.getRequestURI();
        return path.startsWith("/auth") || 
               path.startsWith("/v3/api-docs") || 
               path.startsWith("/swagger-ui");
    }
}
