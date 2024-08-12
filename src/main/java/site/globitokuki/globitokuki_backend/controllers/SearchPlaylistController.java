package site.globitokuki.globitokuki_backend.controllers;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;
import site.globitokuki.globitokuki_backend.dtos.selenium.CookiesDTO;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.*;
import site.globitokuki.globitokuki_backend.services.SearchPlaylistService;
import site.globitokuki.globitokuki_backend.utils.DataUtils;

@RestController
@RequestMapping("/globitokuki/search")
@Log
public class SearchPlaylistController {
  @Value("${APP_PRODUCTION}")
  private Boolean isProduction;
  @Value("${FRONTEND_URL1}")
  private String frontendUrl1;
  @Value("${FRONTEND_URL2}")
  private String frontendUrl2;

  private List<String> allowedOrigins;

  private SearchPlaylistService searchPlaylistService;

  public SearchPlaylistController(SearchPlaylistService searchPlaylistService) {
    this.searchPlaylistService = searchPlaylistService;
  }

  @PostConstruct
  private void init() {
    this.allowedOrigins = Arrays.asList(frontendUrl1, frontendUrl2);
  }

  @PostMapping("/save-cookies")
  public ResponseEntity<String> saveCookiesInDb(HttpServletRequest request, @RequestBody List<CookiesDTO> cookies) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    if (this.isProduction) {
      return new ResponseEntity<>(new ResponseDTO("No permitido en producción", 403), HttpStatus.FORBIDDEN);
    } else {
      try {
        searchPlaylistService.saveCookiesInDb(cookies);
        return new ResponseEntity<>("Cookies guardadas", HttpStatus.OK);
      } catch (Exception e) {
        return new ResponseEntity<>("Failed to save cookies: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @GetMapping("/google")
  public ResponseEntity<String> searchGoogle(HttpServletRequest request, @RequestParam String playlistYtUrl) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    if (this.isProduction) {
      return new ResponseEntity<>(new ResponseDTO("No permitido en producción", 403), HttpStatus.FORBIDDEN);
    } else {
      try {
        searchPlaylistService.searchGoogle(playlistYtUrl);
        return new ResponseEntity<>("Search completed successfully.", HttpStatus.OK);
      } catch (Exception e) {
        return new ResponseEntity<>("Search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @GetMapping("/playlist-images")
  public ResponseEntity<?> searchGoogleImages(HttpServletRequest request, @RequestParam String query) {

    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    if (this.isProduction) {
      return new ResponseEntity<>(new ResponseDTO("No permitido en producción", 403), HttpStatus.FORBIDDEN);
    } else {
      try {
        List<String> response = searchPlaylistService.searchGoogleImages(query);
        return new ResponseEntity<>(response, HttpStatus.OK);
      } catch (InvalidThumbnail e) {
        return new ResponseEntity<>("Search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

  @GetMapping("/playlist-data")
  public ResponseEntity<?> searchYtPlaylistData(HttpServletRequest request, @RequestParam String playlistYtUrl) {

    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    if (this.isProduction) {
      return new ResponseEntity<>(new ResponseDTO("No permitido en producción", 403), HttpStatus.FORBIDDEN);
    } else {
      try {
        PlaylistDTO response = searchPlaylistService.searchYtPlaylistData(playlistYtUrl);
        return new ResponseEntity<>(response, HttpStatus.OK);
      } catch (PlaylistNotFound e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
      } catch (TimeoutException e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 408), HttpStatus.REQUEST_TIMEOUT);
      } catch (Exception e) {
        return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
  }

}
