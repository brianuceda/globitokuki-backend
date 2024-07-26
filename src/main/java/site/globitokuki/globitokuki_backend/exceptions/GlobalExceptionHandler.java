package site.globitokuki.globitokuki_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.RequiredFieldMissing;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistAlreadyExists;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistNotFound;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.RepeatedChapters;
import site.globitokuki.globitokuki_backend.exceptions.SecurityExceptions.ProtectedResource;
import site.globitokuki.globitokuki_backend.exceptions.SecurityExceptions.SQLInjectionException;

@ControllerAdvice
public class GlobalExceptionHandler {
  // Security
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

  // General
  @ExceptionHandler(RequiredFieldMissing.class)
  public ResponseEntity<?> handleProtectedResource(RequiredFieldMissing ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 401);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
  
  // Playlist
  @ExceptionHandler(PlaylistNotFound.class)
  public ResponseEntity<?> handlePlaylistNotFound(PlaylistNotFound ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 404);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }

  @ExceptionHandler(PlaylistAlreadyExists.class)
  public ResponseEntity<?> handlePlaylistAlreadyExists(PlaylistAlreadyExists ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 403);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
  
  @ExceptionHandler(RepeatedChapters.class)
  public ResponseEntity<?> handleRepeatedChapters(RepeatedChapters ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 403);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
}
