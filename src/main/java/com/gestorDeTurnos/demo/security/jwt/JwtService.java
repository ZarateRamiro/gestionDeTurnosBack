package com.gestorDeTurnos.demo.security.jwt;

import com.gestorDeTurnos.demo.model.UsuarioSistema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {
  private static final long EXPIRATION_SECONDS = 8 * 60 * 60;
  private static final Pattern SUBJECT = Pattern.compile("\\\"sub\\\":\\\"([^\\\"]+)\\\"");
  private static final Pattern ROLE = Pattern.compile("\\\"rol\\\":\\\"([A-Z_]+)\\\"");
  private static final Pattern EXPIRATION = Pattern.compile("\\\"exp\\\":(\\d+)");
  private final byte[] secret;

  public JwtService(@Value("${app.jwt.secret:}") String configuredSecret) {
    if (configuredSecret == null || configuredSecret.isBlank()) {
      this.secret = new byte[32];
      new SecureRandom().nextBytes(this.secret);
      System.err.println("WARN: JWT_SECRET no está configurado; los tokens se invalidarán al reiniciar la aplicación.");
    } else {
      this.secret = configuredSecret.getBytes(StandardCharsets.UTF_8);
      if (this.secret.length < 32) throw new IllegalArgumentException("JWT_SECRET debe tener al menos 32 bytes.");
    }
  }

  public String generateToken(UsuarioSistema user) {
    long expiration = Instant.now().getEpochSecond() + EXPIRATION_SECONDS;
    String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
    String payload = encode("{\"sub\":\"" + user.getEmail() + "\",\"rol\":\"" + user.getRol().name() + "\",\"exp\":" + expiration + "}");
    String content = header + "." + payload;
    return content + "." + Base64.getUrlEncoder().withoutPadding().encodeToString(sign(content));
  }

  public Claims validate(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length != 3) return null;
      String content = parts[0] + "." + parts[1];
      byte[] signature = Base64.getUrlDecoder().decode(parts[2]);
      if (!MessageDigest.isEqual(sign(content), signature)) return null;
      String json = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
      String email = capture(SUBJECT, json);
      String role = capture(ROLE, json);
      String exp = capture(EXPIRATION, json);
      if (email == null || role == null || exp == null || Long.parseLong(exp) <= Instant.now().getEpochSecond()) return null;
      return new Claims(email, role);
    } catch (RuntimeException exception) {
      return null;
    }
  }

  public long getExpirationSeconds() { return EXPIRATION_SECONDS; }
  private String encode(String value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8)); }
  private byte[] sign(String value) {
    try {
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secret, "HmacSHA256"));
      return mac.doFinal(value.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) { throw new IllegalStateException("No se pudo firmar el token JWT", e); }
  }
  private String capture(Pattern pattern, String json) { Matcher matcher = pattern.matcher(json); return matcher.find() ? matcher.group(1) : null; }
  public record Claims(String email, String rol) {}
}
