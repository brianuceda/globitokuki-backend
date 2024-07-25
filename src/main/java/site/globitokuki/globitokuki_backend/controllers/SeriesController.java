package site.globitokuki.globitokuki_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.dtos.ResponseDTO;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistAlreadyExists;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistNotFound;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.RequiredFieldMissing;
import site.globitokuki.globitokuki_backend.services.PlaylistService;

@RestController
@RequestMapping("/globitokuki/series/playlist")
public class SeriesController {
  private PlaylistService playlistService;

  public SeriesController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllPlaylists(HttpServletRequest request) {
    List<PlaylistDTO> response = playlistService.getAllPlaylists();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/specific")
  public ResponseEntity<?> getPlaylist(HttpServletRequest request, @RequestParam String identifier) {
    PlaylistDTO response = playlistService.getPlaylist(identifier);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createPlaylist(HttpServletRequest request, @RequestBody PlaylistDTO playlistDTO) {
    try {
      String response = playlistService.createPlaylist(playlistDTO);
      return new ResponseEntity<>(new ResponseDTO(response, 201), HttpStatus.CREATED);
    } catch (RequiredFieldMissing e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.BAD_REQUEST);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> updatePlaylist(HttpServletRequest request, @RequestParam String identifier,
      @RequestBody PlaylistDTO playlistDTO) {
    try {
      String response = playlistService.updatePlaylist(identifier, playlistDTO);
      return new ResponseEntity<>(new ResponseDTO(response, 200), HttpStatus.OK);
    } catch (RequiredFieldMissing e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.BAD_REQUEST);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    }
  }
}
