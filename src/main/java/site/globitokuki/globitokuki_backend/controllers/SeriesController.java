package site.globitokuki.globitokuki_backend.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import site.globitokuki.globitokuki_backend.dtos.*;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.*;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.*;
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
    return new ResponseEntity<>(playlistService.getAllPlaylists(), HttpStatus.OK);
  }

  @GetMapping("/specific")
  public ResponseEntity<?> getPlaylist(HttpServletRequest request, @RequestParam String identifier) {
    return new ResponseEntity<>(playlistService.getPlaylist(identifier), HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<?> createPlaylist(HttpServletRequest request, @RequestBody PlaylistDTO playlistDTO) {
    try {
      // Validate if no have repeated chapters
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());
      return new ResponseEntity<>(new ResponseDTO(playlistService.createPlaylist(playlistDTO), 201), HttpStatus.CREATED);
    } catch (RequiredFieldMissing e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
    } catch (RepeatedChapters e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 403), HttpStatus.FORBIDDEN);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> updatePlaylist(HttpServletRequest request, @RequestParam String identifier,
      @RequestBody PlaylistDTO playlistDTO) {
    try {
      // Validate if no have repeated chapters
      this.validateNoHaveRepeatedChapters(playlistDTO.getChapterList());
      return new ResponseEntity<>(new ResponseDTO(playlistService.updatePlaylist(identifier, playlistDTO), 200), HttpStatus.OK);
    } catch (RequiredFieldMissing e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 400), HttpStatus.BAD_REQUEST);
    } catch (RepeatedChapters e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 403), HttpStatus.FORBIDDEN);
    } catch (PlaylistNotFound e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 404), HttpStatus.NOT_FOUND);
    } catch (PlaylistAlreadyExists e) {
      return new ResponseEntity<>(new ResponseDTO(e.getMessage(), 409), HttpStatus.CONFLICT);
    }
  }

  private void validateNoHaveRepeatedChapters(List<ChapterDTO> chapterList) throws RequiredFieldMissing {
    for (ChapterDTO chapter : chapterList) {
      for (ChapterDTO chapter2 : chapterList) {
        if (chapter.getChapterNumber() == chapter2.getChapterNumber() && chapter != chapter2) {
          throw new RepeatedChapters("No se pueden repetir los capítulos.");
        }
      }
    }
  }
}
