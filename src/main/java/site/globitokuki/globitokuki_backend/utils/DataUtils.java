package site.globitokuki.globitokuki_backend.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import site.globitokuki.exceptions.SecurityExceptions.ProtectedResource;
import site.globitokuki.exceptions.SecurityExceptions.SQLInjectionException;

public class DataUtils {
  @Value("${APP_PRODUCTION}")
  private Boolean isProduction;

  public static String getClientIp() {
    HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
        .getRequest();
    String ipAddress = request.getHeader("X-Forwarded-For");

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }
    if (ipAddress != null && ipAddress.contains(",")) {
      ipAddress = ipAddress.split(",")[0].trim();
    }

    return ipAddress;
  }

  public static void verifyAllowedOrigin(List<String> allowedOrigins, String origin) {
    if (origin == null || !allowedOrigins.contains(origin)) {
      throw new ProtectedResource("Acceso no autorizado.");
    }
  }

  public static void verifySQLInjection(String str) {
    if (str.matches(".*(--|[;+*^$|?{}\\[\\]()'\"\\']).*") || str.contains("SELECT") || str.contains("DELETE")
        || str.contains("UPDATE") || str.contains("INSERT") || str.contains("DROP") || str.isEmpty() || str.isBlank()
        || str == null) {
      throw new SQLInjectionException("Esas cosas son del diablo.");
    }
  }
}
