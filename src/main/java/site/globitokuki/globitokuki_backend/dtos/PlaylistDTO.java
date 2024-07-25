package site.globitokuki.globitokuki_backend.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.globitokuki.globitokuki_backend.entity.PlaylistEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistDTO {
  private String realName;
  private String fullName;
  private String shortName;
  private Integer orderView;
  private Integer chapters;
  private String startViewingDate;
  private String endViewingDate;
  private String playlistLink;
  private String thumbnail;
  private List<ChapterDTO> chapterList;

  public PlaylistEntity convertToEntity() {
    PlaylistEntity playlistEntity = new PlaylistEntity();
    playlistEntity.setRealName(this.realName);
    playlistEntity.setFullName(this.fullName);
    playlistEntity.setShortName(this.shortName);
    playlistEntity.setOrderView(this.orderView);
    playlistEntity.setStartViewingDate(this.startViewingDate);
    playlistEntity.setEndViewingDate(this.endViewingDate);
    playlistEntity.setPlaylistLink(this.playlistLink);
    playlistEntity.setThumbnail(this.thumbnail);
    playlistEntity.setChapterList(ChapterDTO.convertToEntityList(this.chapterList));
    return playlistEntity;
  }

  public static List<PlaylistDTO> convertToDTO(List<PlaylistEntity> playlists) {
    List<PlaylistDTO> playlistDTOs = new ArrayList<>();
    for (PlaylistEntity playlist : playlists) {
      PlaylistDTO playlistDTO = new PlaylistDTO();
      playlistDTO.setRealName(playlist.getRealName());
      playlistDTO.setFullName(playlist.getFullName());
      playlistDTO.setShortName(playlist.getShortName());
      playlistDTO.setOrderView(playlist.getOrderView());
      playlistDTO.setChapters(playlist.getChapters());
      playlistDTO.setStartViewingDate(playlist.getStartViewingDate());
      playlistDTO.setEndViewingDate(playlist.getEndViewingDate());
      playlistDTO.setPlaylistLink(playlist.getPlaylistLink());
      playlistDTO.setThumbnail(playlist.getThumbnail());
      playlistDTOs.add(playlistDTO);
    }
    return playlistDTOs;
  }

  // convert to dto one entity
  public static PlaylistDTO convertToDTO(PlaylistEntity playlist) {
    PlaylistDTO playlistDTO = new PlaylistDTO();
    playlistDTO.setRealName(playlist.getRealName());
    playlistDTO.setFullName(playlist.getFullName());
    playlistDTO.setShortName(playlist.getShortName());
    playlistDTO.setOrderView(playlist.getOrderView());
    playlistDTO.setChapters(playlist.getChapters());
    playlistDTO.setStartViewingDate(playlist.getStartViewingDate());
    playlistDTO.setEndViewingDate(playlist.getEndViewingDate());
    playlistDTO.setPlaylistLink(playlist.getPlaylistLink());
    playlistDTO.setThumbnail(playlist.getThumbnail());
    playlistDTO.setChapterList(ChapterDTO.convertToDTO(playlist.getChapterList()));
    return playlistDTO;
  }
}
