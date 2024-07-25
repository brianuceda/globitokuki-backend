package site.globitokuki.globitokuki_backend.services;

import java.util.List;

import site.globitokuki.globitokuki_backend.dtos.PlaylistDTO;

public interface PlaylistService {
  public List<PlaylistDTO> getAllPlaylists();
  public PlaylistDTO getPlaylist(String identifier);
  public String createPlaylist(PlaylistDTO playlistDTO);
  public String updatePlaylist(String identifier, PlaylistDTO playlistDTO);
}