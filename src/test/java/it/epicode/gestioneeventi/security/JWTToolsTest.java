package it.epicode.gestioneeventi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class JWTToolsTest {

    @Autowired
    private JWTTools jwtTools;

    private Long testUserId = 123L;

    @Test
    public void testGenerateToken() {
        String token = jwtTools.generateToken(testUserId);
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    public void testVerifyValidToken() {
        String token = jwtTools.generateToken(testUserId);
        assertDoesNotThrow(() -> jwtTools.verifyToken(token));
    }

    @Test
    public void testVerifyInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, () -> jwtTools.verifyToken(invalidToken));
    }

    @Test
    public void testExtractIdFromToken() {
        String token = jwtTools.generateToken(testUserId);
        Long extractedId = jwtTools.extractIdFromToken(token);
        assertEquals(testUserId, extractedId);
    }

    @Test
    public void testExtractIdFromInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertThrows(Exception.class, () -> jwtTools.extractIdFromToken(invalidToken));
    }
}
