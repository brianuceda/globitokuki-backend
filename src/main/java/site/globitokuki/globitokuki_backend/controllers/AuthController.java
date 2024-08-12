package site.globitokuki.globitokuki_backend.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import site.globitokuki.globitokuki_backend.dtos.*;
import site.globitokuki.globitokuki_backend.services.AuthService;
import site.globitokuki.globitokuki_backend.utils.DataUtils;

@RestController
@RequestMapping("/globitokuki/auth")
public class AuthController {
  @Value("${FRONTEND_URL1}")
  private String frontendUrl1;
  @Value("${FRONTEND_URL2}")
  private String frontendUrl2;

  private List<String> allowedOrigins;

  private AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostConstruct
  private void init() {
    this.allowedOrigins = Arrays.asList(frontendUrl1, frontendUrl2);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(HttpServletRequest request, @RequestBody AuthRequestDTO requestBody) {
    this.validationsAuth(request, requestBody);

    ResponseDTO response = this.authService.login(requestBody);
    HttpStatus status = HttpStatus.valueOf(response.getCode());

    response.setCode(null);
    return new ResponseEntity<>(response, status);
  }

  // @PostMapping("/register")
  // public ResponseEntity<?> register(HttpServletRequest request, @RequestBody AuthRequestDTO requestBody) {
  //   this.validationsAuth(request, requestBody);

  //   ResponseDTO response = this.authService.register(requestBody);
  //   HttpStatus status = HttpStatus.valueOf(response.getCode());
  
  //   response.setCode(null);
  //   return new ResponseEntity<>(response, status);
  // }

  // Verify if the token is valid and exists in the database
  @PostMapping("/verify")
  public ResponseEntity<?> verify(HttpServletRequest request, @RequestBody AuthResponseDTO requestBody) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));
    DataUtils.verifySQLInjection(requestBody.getToken());

    ResponseDTO response = this.authService.verify(requestBody);
    HttpStatus status = HttpStatus.valueOf(response.getCode());
    
    response.setCode(null);
    return new ResponseEntity<>(response, status);
  }

  private void validationsAuth(HttpServletRequest request, AuthRequestDTO requestBody) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));
    DataUtils.verifySQLInjection(requestBody.getUsername());
    DataUtils.verifySQLInjection(requestBody.getPassword());
  }
}
