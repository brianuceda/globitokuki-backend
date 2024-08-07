package site.globitokuki.globitokuki_backend.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;
import site.globitokuki.globitokuki_backend.entities.enums.StatesEnum;

public interface PlaylistService {
  List<PlaylistDTO> getAllPlaylists();
  PlaylistDTO getPlaylist(String identifier);
  String createPlaylist(PlaylistDTO playlistDTO, MultipartFile thumbnail);
  String updatePlaylist(String identifier, PlaylistDTO playlistDTO, MultipartFile thumbnail);
  String updatePlaylistState(String identifier, StatesEnum state);
  String updateLastViewed(String identifier, String lastChapterViewed, String lastChapterViewedTotalTime, String lastChapterViewedActualTime);
  String deletePlaylist(String identifier);
}
