package site.globitokuki.globitokuki_backend.utils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class JwtUtils {
  @Value("${JWT_SECRET_KEY}")
  private String jwtSecretKey;
  @Value("${JWT_EXP_TIME}")
  private String jwtExpTime;

  public String getTokenFromRequest(HttpServletRequest request) {
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    } else {
      return null;
    }
  }

  public String genToken(UserDetails user) {
    Map<String, Object> extraClaims = new HashMap<>();
    return genToken(extraClaims, user);
  }

  private String genToken(Map<String, Object> extraClaims, UserDetails user) {
    return Jwts
      .builder()
      .setClaims(extraClaims)
      .setSubject(user.getUsername())
      .setIssuedAt(new Date(System.currentTimeMillis()))
      .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(jwtExpTime)))
      .signWith(genTokenSign(), SignatureAlgorithm.HS256)
      .compact();
  }

  private Key genTokenSign() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = getUsernameFromToken(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return getExpiration(token).before(new Date());
  }

  private Claims getAllClaims(String token) {
    return Jwts
      .parserBuilder()
      .setSigningKey(genTokenSign())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = getAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String getUsernameFromToken(String token) {
    return getClaim(token, Claims::getSubject);
  }

  public String getUsernameFromBearerToken(String bearerToken) {
    String realToken = bearerToken.replace("Bearer ", "");
    return getUsernameFromToken(realToken);
  }

  private Date getExpiration(String token) {
    return getClaim(token, Claims::getExpiration);
  }
}