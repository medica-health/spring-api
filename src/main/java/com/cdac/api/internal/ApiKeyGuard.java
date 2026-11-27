package com.cdac.api.internal;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Constant-time check of the X-Api-Key header against the configured internal key. */
public class ApiKeyGuard {

    public enum Outcome { OK, UNAUTHORIZED, DISABLED }

    private final byte[] expected;

    public ApiKeyGuard(String configuredKey) {
        this.expected = configuredKey == null ? new byte[0] : configuredKey.getBytes(StandardCharsets.UTF_8);
    }

    public Outcome check(String header) {
        if (expected.length == 0) {
            return Outcome.DISABLED;
        }
        if (header == null) {
            return Outcome.UNAUTHORIZED;
        }
        return MessageDigest.isEqual(expected, header.getBytes(StandardCharsets.UTF_8))
                ? Outcome.OK : Outcome.UNAUTHORIZED;
    }
}
