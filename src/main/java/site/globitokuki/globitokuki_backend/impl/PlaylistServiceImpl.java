package site.globitokuki.globitokuki_backend.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import site.globitokuki.globitokuki_backend.dtos.ChapterDTO;
import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.entity.ChapterEntity;
import site.globitokuki.globitokuki_backend.entity.PlaylistEntity;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistAlreadyExists;
import site.globitokuki.globitokuki_backend.exceptions.PlaylistExceptions.PlaylistNotFound;
import site.globitokuki.globitokuki_backend.exceptions.GeneralExceptions.RequiredFieldMissing;
import site.globitokuki.globitokuki_backend.repositories.PlaylistRepository;
import site.globitokuki.globitokuki_backend.services.PlaylistService;

@Service
public class PlaylistServiceImpl implements PlaylistService {
  private final PlaylistRepository playlistRepository;

  public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
    this.playlistRepository = playlistRepository;
  }

  @Override
  public List<PlaylistDTO> getAllPlaylists() {
    List<PlaylistEntity> playlists = playlistRepository.findAll();
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
  public String createPlaylist(PlaylistDTO playlistDTO) {
    validateRequiredFields(playlistDTO);

    // Verificar unicidad
    if (existsByUniqueFields(playlistDTO)) {
      throw new PlaylistAlreadyExists("Playlist duplicada.");
    }

    PlaylistEntity playlistEntity = playlistDTO.convertToEntity();

    // Establecer la relación bidireccional entre capítulos y playlist
    List<ChapterEntity> chapterEntities = playlistEntity.getChapterList();
    if (chapterEntities != null) {
      for (ChapterEntity chapterEntity : chapterEntities) {
        chapterEntity.setPlaylist(playlistEntity);
      }
    }

    playlistRepository.save(playlistEntity);
    return "Playlist creada correctamente.";
  }

  @Override
  public String updatePlaylist(String identifier, PlaylistDTO playlistDTO) {
    validateRequiredFields(playlistDTO);

    Optional<PlaylistEntity> optionalPlaylist = playlistRepository.findByFullName(identifier);
    if (!optionalPlaylist.isPresent()) {
      optionalPlaylist = playlistRepository.findByShortName(identifier);
    }

    if (!optionalPlaylist.isPresent()) {
      throw new PlaylistNotFound("Playlist inválida");
    }

    PlaylistEntity existingPlaylist = optionalPlaylist.get();

    // Verificar unicidad excluyendo la playlist actual
    if (!isUniqueFieldsValidForUpdate(playlistDTO, existingPlaylist)) {
      throw new PlaylistAlreadyExists("Playlist duplicada.");
    }

    // Solo actualiza los campos que son diferentes
    if (playlistDTO.getRealName() != null && !playlistDTO.getRealName().isEmpty()
        && !existingPlaylist.getRealName().equals(playlistDTO.getRealName())) {
      existingPlaylist.setRealName(playlistDTO.getRealName());
    }
    if (playlistDTO.getFullName() != null && !playlistDTO.getFullName().isEmpty()
        && !existingPlaylist.getFullName().equals(playlistDTO.getFullName())) {
      existingPlaylist.setFullName(playlistDTO.getFullName());
    }
    if (playlistDTO.getShortName() != null && !playlistDTO.getShortName().isEmpty()
        && !existingPlaylist.getShortName().equals(playlistDTO.getShortName())) {
      existingPlaylist.setShortName(playlistDTO.getShortName());
    }
    if (playlistDTO.getOrderView() != null && !existingPlaylist.getOrderView().equals(playlistDTO.getOrderView())) {
      existingPlaylist.setOrderView(playlistDTO.getOrderView());
    }
    if (playlistDTO.getStartViewingDate() != null
        && !existingPlaylist.getStartViewingDate().equals(playlistDTO.getStartViewingDate())) {
      existingPlaylist.setStartViewingDate(playlistDTO.getStartViewingDate());
    }
    if (playlistDTO.getEndViewingDate() != null
        && !existingPlaylist.getEndViewingDate().equals(playlistDTO.getEndViewingDate())) {
      existingPlaylist.setEndViewingDate(playlistDTO.getEndViewingDate());
    }
    if (playlistDTO.getPlaylistLink() != null
        && !existingPlaylist.getPlaylistLink().equals(playlistDTO.getPlaylistLink())) {
      existingPlaylist.setPlaylistLink(playlistDTO.getPlaylistLink());
    }
    if (playlistDTO.getThumbnail() != null && !existingPlaylist.getThumbnail().equals(playlistDTO.getThumbnail())) {
      existingPlaylist.setThumbnail(playlistDTO.getThumbnail());
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
      for (int i = 0; i < newChapters.size(); i++) {
        ChapterDTO newChapterDTO = newChapters.get(i);
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
    playlistRepository.save(existingPlaylist);
    return "Playlist actualizada correctamente.";
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

  private boolean isUniqueFieldsValidForUpdate(PlaylistDTO playlistDTO, PlaylistEntity existingPlaylist) {
    return !(playlistRepository.findByFullName(playlistDTO.getFullName())
        .filter(p -> !p.getId().equals(existingPlaylist.getId())).isPresent()
        || playlistRepository.findByShortName(playlistDTO.getShortName())
            .filter(p -> !p.getId().equals(existingPlaylist.getId())).isPresent());
  }
}
