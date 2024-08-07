package site.globitokuki.globitokuki_backend.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import site.globitokuki.globitokuki_backend.dtos.*;
import site.globitokuki.globitokuki_backend.entities.enums.StatesEnum;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.*;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.*;
import site.globitokuki.globitokuki_backend.services.PlaylistService;
import site.globitokuki.globitokuki_backend.utils.DataUtils;

@RestController
@RequestMapping("/globitokuki/series/playlist")
@Log
public class SeriesController {
  @Value("${FRONTEND_URL1}")
  private String frontendUrl1;
  @Value("${FRONTEND_URL2}")
  private String frontendUrl2;

  private List<String> allowedOrigins;

  private PlaylistService playlistService;

  public SeriesController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  @PostConstruct
  private void init() {
    this.allowedOrigins = Arrays.asList(frontendUrl1, frontendUrl2);
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllPlaylists(HttpServletRequest request) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    return new ResponseEntity<>(playlistService.getAllPlaylists(), HttpStatus.OK);
  }

  @GetMapping("/specific/{identifier}")
  public ResponseEntity<?> getPlaylist(HttpServletRequest request,
      @PathVariable String identifier) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    return new ResponseEntity<>(playlistService.getPlaylist(identifier), HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createPlaylist(HttpServletRequest request, 
      @RequestPart("playlistDTO") PlaylistDTO playlistDTO,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    try {
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());

      if (playlistDTO.getFullName() == null ||
          playlistDTO.getFullName().isEmpty() || playlistDTO.getFullName().isBlank() ||
          playlistDTO.getShortName() == null ||
          playlistDTO.getShortName().isEmpty() || playlistDTO.getShortName().isBlank()) {
        throw new RequiredFieldMissing("Faltan campos requeridos.");
      }

      if (playlistDTO.getFullName().equals("nueva") ||
          playlistDTO.getShortName().equals("nueva")) {
        throw new InvalidName("Nombre no permitido.");
      }

      String response = playlistService.createPlaylist(playlistDTO, thumbnail);
      return new ResponseEntity<>(new ResponseDTO(response, 201), HttpStatus.CREATED);
    } catch (RequiredFieldMissing | InvalidName e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
    } catch (RepeatedChapters e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 403), HttpStatus.FORBIDDEN);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    } catch (InvalidThumbnail e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 415), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> updatePlaylist(HttpServletRequest request, 
      @RequestParam String identifier,
      @RequestPart("playlistDTO") PlaylistDTO playlistDTO,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    try {
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());
      String response = playlistService.updatePlaylist(identifier, playlistDTO, thumbnail);
      return new ResponseEntity<>(new ResponseDTO(response, 200), HttpStatus.OK);
    } catch (RequiredFieldMissing e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
    } catch (RepeatedChapters e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 403), HttpStatus.FORBIDDEN);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    } catch (InvalidThumbnail e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 415), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
  }

  @PutMapping("/update/state")
  public ResponseEntity<?> updatePlaylistState(HttpServletRequest request, 
      @RequestParam String identifier,
      @RequestParam StatesEnum state) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    try {
      String response = playlistService.updatePlaylistState(identifier, state);
      return new ResponseEntity<>(new ResponseDTO(response, 200), HttpStatus.OK);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    }
  }

  @PutMapping("/update/last-viewed")
  public ResponseEntity<?> updateLastViewed(HttpServletRequest request, 
      @RequestParam String identifier,
      @RequestParam String lastChapterViewed,
      @RequestParam String lastChapterViewedTotalTime,
      @RequestParam String lastChapterViewedActualTime) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    try {
      String response = playlistService.updateLastViewed(
          identifier, lastChapterViewed, lastChapterViewedTotalTime, lastChapterViewedActualTime);
      return new ResponseEntity<>(new ResponseDTO(response, 200), HttpStatus.OK);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deletePlaylist(HttpServletRequest request, 
      @RequestParam String identifier) {
    DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    try {
      String response = playlistService.deletePlaylist(identifier);
      return new ResponseEntity<>(new ResponseDTO(response, 200), HttpStatus.OK);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    }
  }

  private void validateNoHaveRepeatedChapters(List<ChapterDTO> chapterList) throws RequiredFieldMissing {
    if (chapterList != null && !chapterList.isEmpty()) {
      for (ChapterDTO chapter : chapterList) {
        for (ChapterDTO chapter2 : chapterList) {
          if (chapter.getChapterNumber() == chapter2.getChapterNumber() && chapter != chapter2) {
            throw new RepeatedChapters("No se pueden repetir los capítulos.");
          }
        }
      }
    }
  }
}
