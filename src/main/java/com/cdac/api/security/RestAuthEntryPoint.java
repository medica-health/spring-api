package com.cdac.api.security;

/**
 * SKELETON. Will implement {@code AuthenticationEntryPoint}: called when a protected route
 * is hit without valid credentials.
 *
 * <p>TODO: respond {@code 401} with {@code Content-Type: application/json} and the standard
 * envelope, written with the Spring-managed ObjectMapper:
 * <pre>{"success":false,"message":"Authentication required","timestamp":"..."}</pre>
 * For an expired token (an {@code InvalidBearerTokenException} whose cause is a
 * {@code JwtValidationException} mentioning expiry), use the message {@code "Token expired"}.
 * The client relies on that message to trigger a refresh, the same as express-api.
 */
public class RestAuthEntryPoint {
}
