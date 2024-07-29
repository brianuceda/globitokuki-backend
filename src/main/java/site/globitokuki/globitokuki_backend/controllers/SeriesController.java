package site.globitokuki.globitokuki_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import site.globitokuki.globitokuki_backend.dtos.*;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.*;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.*;
import site.globitokuki.globitokuki_backend.services.PlaylistService;

@RestController
@RequestMapping("/globitokuki/series/playlist")
@Log
public class SeriesController {
  private PlaylistService playlistService;

  public SeriesController(PlaylistService playlistService) {
    this.playlistService = playlistService;
  }

  @GetMapping("/all")
  public ResponseEntity<?> getAllPlaylists(HttpServletRequest request) {
    return new ResponseEntity<>(playlistService.getAllPlaylists(), HttpStatus.OK);
  }

  @GetMapping("/specific")
  public ResponseEntity<?> getPlaylist(HttpServletRequest request, @RequestParam String identifier) {
    return new ResponseEntity<>(playlistService.getPlaylist(identifier), HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createPlaylist(
      @RequestPart("playlistDTO") PlaylistDTO playlistDTO,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    try {
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());

      System.out.println("creando: " + playlistDTO);

      String response = playlistService.createPlaylist(playlistDTO, thumbnail);
      return new ResponseEntity<>(new ResponseDTO(response, 201), HttpStatus.CREATED);
    } catch (RequiredFieldMissing e) {
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
  public ResponseEntity<?> updatePlaylist(
      @RequestParam String identifier,
      @RequestPart("playlistDTO") PlaylistDTO playlistDTO,
      @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    try {
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());

      System.out.println("actualizando: " + playlistDTO);

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

  @DeleteMapping("/delete")
  public ResponseEntity<?> deletePlaylist(@RequestParam String identifier) {
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
