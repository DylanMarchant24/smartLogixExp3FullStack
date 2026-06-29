package cl.duocuc.smartlogix.bff.dto;

public record AuthResponse(String token, String tokenType, String username, long expiresInMinutes) {
}