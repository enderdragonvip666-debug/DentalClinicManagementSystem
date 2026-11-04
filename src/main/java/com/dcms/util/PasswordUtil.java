package com.dcms.util;

import org.mindrot.jbcrypt.BCrypt;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** BCrypt hashing and one-time reset-token / request-code generation. */
public final class PasswordUtil {
    private static final SecureRandom RND = new SecureRandom();
    private PasswordUtil() {}
    public static String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt(10)); }
    public static boolean matches(String raw, String hash) {
        try { return raw != null && hash != null && BCrypt.checkpw(raw, hash); } catch (Exception e) { return false; }
    }
    public static String newToken() {
        byte[] b = new byte[32]; RND.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
    public static String sha256(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte x : d) sb.append(String.format("%02x", x));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    public static String requestCode() {
        String abc = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; StringBuilder sb = new StringBuilder("LH-");
        for (int i = 0; i < 6; i++) sb.append(abc.charAt(RND.nextInt(abc.length())));
        return sb.toString();
    }
}
