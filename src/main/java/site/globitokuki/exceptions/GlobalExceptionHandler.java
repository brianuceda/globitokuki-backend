package site.globitokuki.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import site.globitokuki.exceptions.SecurityExceptions.ProtectedResource;
import site.globitokuki.exceptions.SecurityExceptions.SQLInjectionException;
import site.globitokuki.globitokuki_backend.dto.ResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ProtectedResource.class)
  public ResponseEntity<?> handleProtectedResource(ProtectedResource ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 401);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
  
  @ExceptionHandler(SQLInjectionException.class)
  public ResponseEntity<?> handleSQLInjectionException(SQLInjectionException ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 401);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
}
