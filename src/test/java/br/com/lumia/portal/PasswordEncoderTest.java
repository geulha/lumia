package br.com.lumia.portal;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PasswordEncoderTest {

    @Test
    void testBcryptHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("senha123");
        System.out.println("BCRYPT_HASH_FOR_SENHA123=" + hash);
        assertTrue(encoder.matches("senha123", hash));
    }
}
