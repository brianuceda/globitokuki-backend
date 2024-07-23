package site.globitokuki.globitokuki_backend.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import site.globitokuki.globitokuki_backend.dto.AuthRequestDTO;
import site.globitokuki.globitokuki_backend.dto.AuthResponseDTO;
import site.globitokuki.globitokuki_backend.utils.DataUtils;

@RestController
@RequestMapping("/globitokuki/auth")
public class AuthController {
  @Value("${FRONTEND_URL1}")
  private String frontendUrl1;
  @Value("${FRONTEND_URL2}")
  private String frontendUrl2;
  @Value("${GLOBITOKUKI_PIN}")
  private String correctPin;
  
  private List<String> allowedOrigins;
  
  @PostConstruct
  private void init() {
    this.allowedOrigins = Arrays.asList(frontendUrl1, frontendUrl2);
  }

  @PostMapping("/verify")
  public ResponseEntity<AuthResponseDTO> verifyPin(HttpServletRequest request, @RequestBody AuthRequestDTO authRequest) {
    // Validations
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));
    DataUtils.verifySQLInjection(authRequest.getPin());

    // Logic
    boolean isValidPin = correctPin.equals(authRequest.getPin());
    HttpStatus httpStatus = isValidPin ? HttpStatus.OK : HttpStatus.UNAUTHORIZED;
    return new ResponseEntity<>(new AuthResponseDTO(isValidPin), httpStatus);
  }
}
