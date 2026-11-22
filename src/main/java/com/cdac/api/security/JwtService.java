package com.cdac.api.security;

import org.springframework.stereotype.Service;

/**
 * Issues and verifies access tokens. SKELETON: describes the contract only.
 *
 * <h2>Contract with express-api (do not change one side without the other)</h2>
 * <ul>
 *   <li>Algorithm: <b>HS256</b>, key = UTF-8 bytes of {@code app.jwt.secret} ({@code JWT_SECRET},
 *       at least 32 characters, identical in both services).</li>
 *   <li>Header: {@code {"alg":"HS256","typ":"JWT"}}.</li>
 *   <li>Claims:
 *     <ul>
 *       <li>{@code sub}: auth.users.id (UUID string)</li>
 *       <li>{@code email}: the account email</li>
 *       <li>{@code name}: full_name</li>
 *       <li>{@code role}: PATIENT | DOCTOR | RECEPTIONIST | ADMIN</li>
 *       <li>{@code iss}: {@code app.jwt.issuer} (default {@code cdac-auth})</li>
 *       <li>{@code aud}: {@code app.jwt.audience} (default {@code cdac-api})</li>
 *       <li>{@code iat}, {@code exp}: now + {@code app.jwt.access-token-ttl} (default 15m)</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h2>TODO: implementation</h2>
 * <ul>
 *   <li>Build a {@code NimbusJwtEncoder} over an {@code ImmutableSecret} (SecretKeySpec "HmacSHA256").
 *       Fail fast at startup if the secret is blank or shorter than 32 chars.</li>
 *   <li>{@code String issueAccessToken(User user)}: encode the claims above with
 *       {@code JwsHeader.with(MacAlgorithm.HS256)}.</li>
 *   <li>Expose a {@code JwtDecoder} bean ({@code NimbusJwtDecoder.withSecretKey(...).macAlgorithm(HS256)})
 *       with validators for issuer, audience and expiry. SecurityConfig uses it for {@code /api/auth/me}
 *       and {@code /api/admin/**}.</li>
 *   <li>{@code String newOpaqueToken()}: 32 bytes from SecureRandom, Base64URL without padding.
 *       Used for refresh, password-reset and email-verification tokens.</li>
 *   <li>{@code String sha256Hex(String token)}: the value stored in the *_tokens tables. Raw
 *       tokens are never persisted.</li>
 * </ul>
 */
@Service
public class JwtService {
}
