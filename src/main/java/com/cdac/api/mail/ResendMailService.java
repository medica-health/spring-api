package com.cdac.api.mail;

/**
 * Transactional email through Resend (https://resend.com). SKELETON: describes the design.
 * express-api sends appointment emails the same way (src/services/mailer.service.ts), so keep
 * the two consistent: same sender, same layout, same "log and continue" failure policy.
 *
 * <h2>Config ({@code app.mail.*} in application.yml)</h2>
 * <ul>
 *   <li>{@code resend-api-key} ({@code RESEND_API_KEY}). If blank, log
 *       {@code "[mail skipped] to=… subject=…"} and return. That's the local-dev mode.</li>
 *   <li>{@code from} ({@code MAIL_FROM}): must be on a domain verified in Resend.
 *       {@code onboarding@resend.dev} only delivers to the Resend account owner's address.</li>
 *   <li>{@code reply-to} ({@code MAIL_REPLY_TO}, optional), and {@code app-base-url} for links.</li>
 * </ul>
 *
 * <h2>TODO: implementation (no extra dependency: Spring's {@code RestClient})</h2>
 * <pre>
 * POST https://api.resend.com/emails
 * Authorization: Bearer ${RESEND_API_KEY}
 * Idempotency-Key: &lt;optional, e.g. "verify-email/&lt;tokenId&gt;"; Resend de-duplicates for 24h&gt;
 * Content-Type: application/json
 * { "from": "...", "to": ["user@example.com"], "subject": "...", "html": "...", "text": "...",
 *   "reply_to": "...", "tags": [{ "name": "type", "value": "PASSWORD_RESET" }] }
 * → 200 { "id": "&lt;email id&gt;" }   |   4xx/5xx { "name": "...", "message": "...", "statusCode": n }
 * </pre>
 * <ul>
 *   <li>{@code void send(String to, String subject, String html, String text, String type, String idempotencyKey)}</li>
 *   <li>Retry {@code 429 rate_limit_exceeded} and 5xx up to 3 times with backoff (0.5s, 1s). Don't retry
 *       other 4xx errors (invalid_from_address, validation_error).</li>
 *   <li>Never throw to callers. Log failures with the Resend error name. Auth flows (register,
 *       forgot-password) must succeed even if email is down.</li>
 *   <li>Call it after the DB transaction commits ({@code @TransactionalEventListener(phase = AFTER_COMMIT)}
 *       or {@code @Async}), so a rollback never sends an email and the HTTP request doesn't wait on Resend.</li>
 *   <li>Templates: {@code sendPasswordReset(user, rawToken)} links to
 *       {@code {app-base-url}/reset-password?token=…} (expires in 30 min).
 *       {@code sendEmailVerification(user, rawToken)} links to {@code {app-base-url}/verify-email?token=…}
 *       (24h). {@code sendStaffInvite(user, rawToken)} serves admin-created accounts.</li>
 * </ul>
 */
public class ResendMailService {
}
