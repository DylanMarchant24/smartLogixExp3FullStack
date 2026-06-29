package cl.duocuc.smartlogix.bff.controller;

import cl.duocuc.smartlogix.bff.dto.AuthResponse;
import cl.duocuc.smartlogix.bff.dto.LoginRequest;
import cl.duocuc.smartlogix.bff.dto.ValidateResponse;
import cl.duocuc.smartlogix.bff.security.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private static final String TEST_USERNAME = "admin";
    private static final String TEST_PASSWORD = "admin123";

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (!TEST_USERNAME.equals(request.username()) || !TEST_PASSWORD.equals(request.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales invalidas"));
        }

        String token = jwtService.generarToken(request.username());

        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                request.username(),
                jwtService.getExpirationMinutes()
        ));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateResponse(false, null));
        }

        String token = authorizationHeader.substring(7);

        if (!jwtService.validarToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ValidateResponse(false, null));
        }

        return ResponseEntity.ok(new ValidateResponse(true, jwtService.obtenerUsername(token)));
    }
}