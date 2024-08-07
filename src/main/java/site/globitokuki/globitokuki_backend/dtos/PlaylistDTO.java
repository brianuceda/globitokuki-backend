package site.globitokuki.globitokuki_backend.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.globitokuki.globitokuki_backend.entities.ChapterEntity;
import site.globitokuki.globitokuki_backend.entities.ImageEntity;
import site.globitokuki.globitokuki_backend.entities.PlaylistEntity;
import site.globitokuki.globitokuki_backend.entities.enums.StatesEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlaylistDTO {
  private String realName;
  private String fullName;
  private String shortName;

  private Integer orderView;
  private String thumbnail;

  private String startViewingDate;
  private String endViewingDate;
  private String description;
  private String playlistLink;

  private String ourComment;
  private Double starsGlobito;
  private Double starsKuki;

  private StatesEnum state;

  private Integer chapters;
  private List<ChapterDTO> chapterList;

  private Integer lastChapterViewed;
  private String lastChapterViewedTime;
  private String lastChapterTotalTime;

  public PlaylistEntity convertToEntity() {
    PlaylistEntity playlistEntity = new PlaylistEntity();
    playlistEntity.setRealName(this.realName);
    playlistEntity.setFullName(this.fullName);
    playlistEntity.setShortName(this.shortName);
    playlistEntity.setOrderView(this.orderView);
    playlistEntity.setStartViewingDate(this.startViewingDate);
    playlistEntity.setEndViewingDate(this.endViewingDate);
    playlistEntity.setDescription(this.description);
    playlistEntity.setPlaylistLink(this.playlistLink);
    playlistEntity.setOurComment(this.ourComment);
    playlistEntity.setStarsGlobito(this.starsGlobito);
    playlistEntity.setStarsKuki(this.starsKuki);
    playlistEntity.setState(this.state);
    playlistEntity.setLastChapterViewed(this.lastChapterViewed);
    playlistEntity.setLastChapterViewedTime(this.lastChapterViewedTime);
    playlistEntity.setLastChapterTotalTime(this.lastChapterTotalTime);

    if (this.thumbnail != null && !this.thumbnail.isEmpty()) {
      ImageEntity imageEntity = new ImageEntity();
      imageEntity.setImageData(Base64.getDecoder().decode(this.thumbnail));
      playlistEntity.setThumbnail(imageEntity);
    }

    if (playlistEntity.getStarsGlobito() == null) {
      playlistEntity.setStarsGlobito(0.0);
    }
    if (playlistEntity.getStarsKuki() == null) {
      playlistEntity.setStarsKuki(0.0);
    }
    if (playlistEntity.getState() == null) {
      playlistEntity.setState(StatesEnum.NOT_WATCHED);
    }

    if (this.chapterList != null) {
      List<ChapterEntity> chapters = new ArrayList<>();

      for (ChapterDTO chapterDTO : this.chapterList) {
        ChapterEntity chapterEntity = new ChapterEntity();
        chapterEntity.setChapterNumber(chapterDTO.getChapterNumber());
        chapterEntity.setYtId(chapterDTO.getYtId());

        chapterEntity.setViewedTime((chapterDTO.getViewedTime() != null) ? chapterDTO.getViewedTime() : "00:00:00");
        chapterEntity.setTotalTime((chapterDTO.getTotalTime() != null) ? chapterDTO.getTotalTime() : "00:00:00");
        chapterEntity.setHavePreviousChapter(chapterDTO.getHavePreviousChapter());
        chapterEntity.setHaveNextChapter(chapterDTO.getHaveNextChapter());
        chapterEntity.setPlaylist(playlistEntity);
        chapters.add(chapterEntity);
      }

      playlistEntity.setChapterList(chapters);
    }

    return playlistEntity;
  }

  public static List<PlaylistDTO> convertToDTO(List<PlaylistEntity> playlists) {
    List<PlaylistDTO> playlistDTOs = new ArrayList<>();

    for (PlaylistEntity playlist : playlists) {
      playlistDTOs.add(convertToDTO(playlist));
    }

    return playlistDTOs;
  }

  public static PlaylistDTO convertToDTO(PlaylistEntity playlist) {
    PlaylistDTO playlistDTO = new PlaylistDTO();
    playlistDTO.setRealName(playlist.getRealName());
    playlistDTO.setFullName(playlist.getFullName());
    playlistDTO.setShortName(playlist.getShortName());
    playlistDTO.setOrderView(playlist.getOrderView());
    playlistDTO.setStartViewingDate(playlist.getStartViewingDate());
    playlistDTO.setEndViewingDate(playlist.getEndViewingDate());
    playlistDTO.setDescription(playlist.getDescription());
    playlistDTO.setPlaylistLink(playlist.getPlaylistLink());
    playlistDTO.setOurComment(playlist.getOurComment());
    playlistDTO.setStarsKuki(playlist.getStarsKuki());
    playlistDTO.setStarsGlobito(playlist.getStarsGlobito());
    playlistDTO.setState(playlist.getState());
    playlistDTO.setChapters(playlist.getChapters());
    playlistDTO.setLastChapterViewed(playlist.getLastChapterViewed());
    playlistDTO.setLastChapterViewedTime(playlist.getLastChapterViewedTime());
    playlistDTO.setLastChapterTotalTime(playlist.getLastChapterTotalTime());

    ImageEntity imageEntity = playlist.getThumbnail();

    if (imageEntity != null) {
      playlistDTO.setThumbnail(Base64.getEncoder().encodeToString(imageEntity.getImageData()));
    }

    playlistDTO.setChapterList(ChapterDTO.convertEntityListToDTOList(playlist.getChapterList()));

    return playlistDTO;
  }
}
