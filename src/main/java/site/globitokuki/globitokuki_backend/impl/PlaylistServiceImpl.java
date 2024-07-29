package site.globitokuki.globitokuki_backend.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.globitokuki.globitokuki_backend.dtos.ChapterDTO;
import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.entity.ChapterEntity;
import site.globitokuki.globitokuki_backend.entity.ImageEntity;
import site.globitokuki.globitokuki_backend.entity.PlaylistEntity;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.InvalidThumbnail;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistAlreadyExists;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistNotFound;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.RequiredFieldMissing;
import site.globitokuki.globitokuki_backend.repositories.ImageRepository;
import site.globitokuki.globitokuki_backend.repositories.PlaylistRepository;
import site.globitokuki.globitokuki_backend.services.PlaylistService;
// import site.globitokuki.globitokuki_backend.services.SearchPlaylistService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {
  @Autowired
  private final PlaylistRepository playlistRepository;
  @Autowired
  private final ImageRepository imageRepository;
  // @Autowired
  // private final SearchPlaylistService searchPlaylistService;

  @Override
  public List<PlaylistDTO> getAllPlaylists() {
    List<PlaylistEntity> playlists = playlistRepository.findAllByOrderByOrderViewAsc();
    return PlaylistDTO.convertToDTO(playlists);
  }

  @Override
  public PlaylistDTO getPlaylist(String identifier) {
    Optional<PlaylistEntity> optionalPlaylist = playlistRepository.findByFullName(identifier);
    if (!optionalPlaylist.isPresent()) {
      optionalPlaylist = playlistRepository.findByShortName(identifier);
    }

    if (!optionalPlaylist.isPresent()) {
      throw new PlaylistNotFound("Playlist inválida");
    }

    PlaylistEntity playlist = optionalPlaylist.get();
    return PlaylistDTO.convertToDTO(playlist);
  }

  @Override
  public String createPlaylist(PlaylistDTO playlistDTO, MultipartFile thumbnailFile) {
    validateRequiredFields(playlistDTO);

    if (existsByUniqueFields(playlistDTO)) {
      throw new PlaylistAlreadyExists("Playlist duplicada.");
    }

    PlaylistEntity playlistEntity = playlistDTO.convertToEntity();
    if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
      try {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setImageData(thumbnailFile.getBytes());
        imageRepository.save(imageEntity);
        playlistEntity.setThumbnail(imageEntity);
      } catch (IOException e) {
        throw new InvalidThumbnail("Error al procesar el archivo.");
      }
    }

    playlistRepository.save(playlistEntity);
    return "Playlist creada.";
  }

  @Override
  public String updatePlaylist(String identifier, PlaylistDTO playlistDTO, MultipartFile thumbnailFile) {
    validateRequiredFields(playlistDTO);

    Optional<PlaylistEntity> optionalPlaylist = playlistRepository.findByFullName(identifier);
    if (!optionalPlaylist.isPresent()) {
      optionalPlaylist = playlistRepository.findByShortName(identifier);
    }

    if (!optionalPlaylist.isPresent()) {
      throw new PlaylistNotFound("Playlist inválida.");
    }

    PlaylistEntity existingPlaylist = optionalPlaylist.get();
    updatePlaylistEntity(existingPlaylist, playlistDTO, thumbnailFile);
    playlistRepository.save(existingPlaylist);
    return "Playlist actualizada.";
  }

  @Override
  public String deletePlaylist(String identifier) {
    Optional<PlaylistEntity> optionalPlaylist = playlistRepository.findByFullName(identifier);
    if (!optionalPlaylist.isPresent()) {
      optionalPlaylist = playlistRepository.findByShortName(identifier);
    }

    if (!optionalPlaylist.isPresent()) {
      throw new PlaylistNotFound("Playlist inválida.");
    }

    PlaylistEntity playlist = optionalPlaylist.get();
    playlistRepository.delete(playlist);
    return "Playlist eliminada.";
  }

  private void updatePlaylistEntity(PlaylistEntity existingPlaylist, PlaylistDTO playlistDTO,
      MultipartFile thumbnailFile) {
    if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
      try {
        ImageEntity imageEntity = existingPlaylist.getThumbnail();
        if (imageEntity == null) {
          imageEntity = new ImageEntity();
        }
        imageEntity.setImageData(thumbnailFile.getBytes());
        imageRepository.save(imageEntity);
        existingPlaylist.setThumbnail(imageEntity);
      } catch (IOException e) {
        throw new InvalidThumbnail("Error al procesar el archivo.");
      }
    }

    if (playlistDTO.getRealName() != null && !playlistDTO.getRealName().isEmpty()) {
      existingPlaylist.setRealName(playlistDTO.getRealName());
    }
    if (playlistDTO.getFullName() != null && !playlistDTO.getFullName().isEmpty()) {
      existingPlaylist.setFullName(playlistDTO.getFullName());
    }
    if (playlistDTO.getShortName() != null && !playlistDTO.getShortName().isEmpty()) {
      existingPlaylist.setShortName(playlistDTO.getShortName());
    }
    if (playlistDTO.getOrderView() != null) {
      existingPlaylist.setOrderView(playlistDTO.getOrderView());
    }
    if (playlistDTO.getStartViewingDate() != null) {
      existingPlaylist.setStartViewingDate(playlistDTO.getStartViewingDate());
    }
    if (playlistDTO.getEndViewingDate() != null) {
      existingPlaylist.setEndViewingDate(playlistDTO.getEndViewingDate());
    }
    if (playlistDTO.getPlaylistLink() != null && !playlistDTO.getPlaylistLink().isEmpty()) {
      existingPlaylist.setPlaylistLink(playlistDTO.getPlaylistLink());
    }
    if (playlistDTO.getDescription() != null && !playlistDTO.getDescription().isEmpty()) {
      existingPlaylist.setDescription(playlistDTO.getDescription());
    }
    if (playlistDTO.getOurComment() != null && !playlistDTO.getOurComment().isEmpty()) {
      existingPlaylist.setOurComment(playlistDTO.getOurComment());
    }
    if (playlistDTO.getStarsGlobito() != null) {
      existingPlaylist.setStarsGlobito(playlistDTO.getStarsGlobito());
    }
    if (playlistDTO.getStarsKuki() != null) {
      existingPlaylist.setStarsKuki(playlistDTO.getStarsKuki());
    }

    // Actualizar capítulos
    List<ChapterEntity> existingChapters = existingPlaylist.getChapterList();
    List<ChapterDTO> newChapters = playlistDTO.getChapterList();

    // Crear una lista con los números de capítulo de la nueva lista
    List<Integer> newChapterNumbers = newChapters.stream()
        .map(ChapterDTO::getChapterNumber)
        .collect(Collectors.toList());

    // Eliminar capítulos que no están en la nueva lista
    existingChapters.removeIf(chapter -> !newChapterNumbers.contains(chapter.getChapterNumber()));

    // Actualizar o agregar capítulos
    if (newChapters != null) {
      for (ChapterDTO newChapterDTO : newChapters) {
        Optional<ChapterEntity> existingChapterOpt = existingChapters.stream()
            .filter(chapter -> chapter.getChapterNumber().equals(newChapterDTO.getChapterNumber()))
            .findFirst();

        if (existingChapterOpt.isPresent()) {
          ChapterEntity existingChapterEntity = existingChapterOpt.get();
          if (!existingChapterEntity.getYtId().equals(newChapterDTO.getYtId())) {
            existingChapterEntity.setYtId(newChapterDTO.getYtId());
          }
        } else {
          ChapterEntity chapterEntity = newChapterDTO.convertToEntity();
          chapterEntity.setPlaylist(existingPlaylist);
          existingChapters.add(chapterEntity);
        }
      }
    }

    existingPlaylist.setChapterList(existingChapters);
  }

  private void validateRequiredFields(PlaylistDTO playlistDTO) {
    if (playlistDTO.getRealName() == null || playlistDTO.getRealName().isEmpty()) {
      throw new RequiredFieldMissing("Nombre real es requerido");
    }
    if (playlistDTO.getFullName() == null || playlistDTO.getFullName().isEmpty()) {
      throw new RequiredFieldMissing("Nombre completo es requerido");
    }
    if (playlistDTO.getShortName() == null || playlistDTO.getShortName().isEmpty()) {
      throw new RequiredFieldMissing("Nombre corto es requerido");
    }
  }

  private boolean existsByUniqueFields(PlaylistDTO playlistDTO) {
    return playlistRepository.findByFullName(playlistDTO.getFullName()).isPresent()
        || playlistRepository.findByShortName(playlistDTO.getShortName()).isPresent();
  }
}
