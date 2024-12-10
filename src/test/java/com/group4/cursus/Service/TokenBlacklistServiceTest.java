package com.group4.cursus.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TokenBlacklistServiceTest {

    private TokenBlacklistService tokenBlacklistService;

    @BeforeEach
    public void setUp() {
        tokenBlacklistService = new TokenBlacklistService();
    }

    @Test
    public void testBlacklistToken() {
        String token = "testToken123";
        tokenBlacklistService.blacklistToken(token);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token), "Token should be blacklisted");
    }

    @Test
    public void testIsTokenBlacklisted_NotBlacklisted() {
        String token = "testToken123";
        assertFalse(tokenBlacklistService.isTokenBlacklisted(token), "Token should not be blacklisted initially");
    }

    @Test
    public void testIsTokenBlacklisted_AfterBlacklisting() {
        String token = "testToken123";
        tokenBlacklistService.blacklistToken(token);
        assertTrue(tokenBlacklistService.isTokenBlacklisted(token), "Token should be blacklisted after adding");
    }
}
