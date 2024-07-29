package site.globitokuki.globitokuki_backend.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.globitokuki.globitokuki_backend.entity.ImageEntity;
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

    private String description;
    private String ourComment;
    private Double starsGlobito;
    private Double starsKuki;

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
        playlistEntity.setDescription(this.description);
        playlistEntity.setOurComment(this.ourComment);
        playlistEntity.setStarsGlobito(this.starsGlobito);
        playlistEntity.setStarsKuki(this.starsKuki);

        if (this.thumbnail != null && !this.thumbnail.isEmpty()) {
            ImageEntity imageEntity = new ImageEntity();
            imageEntity.setImageData(Base64.getDecoder().decode(this.thumbnail));
            playlistEntity.setThumbnail(imageEntity);
        }

        if (this.chapterList != null && !this.chapterList.isEmpty()) {
            playlistEntity.setChapterList(ChapterDTO.convertToEntityList(this.chapterList));
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
        playlistDTO.setChapters(playlist.getChapters());
        playlistDTO.setStartViewingDate(playlist.getStartViewingDate());
        playlistDTO.setEndViewingDate(playlist.getEndViewingDate());
        playlistDTO.setPlaylistLink(playlist.getPlaylistLink());
        playlistDTO.setDescription(playlist.getDescription());
        playlistDTO.setOurComment(playlist.getOurComment());
        playlistDTO.setStarsKuki(playlist.getStarsKuki());
        playlistDTO.setStarsGlobito(playlist.getStarsGlobito());

        ImageEntity imageEntity = playlist.getThumbnail();
        if (imageEntity != null) {
            playlistDTO.setThumbnail(Base64.getEncoder().encodeToString(imageEntity.getImageData()));
        }

        playlistDTO.setChapterList(ChapterDTO.convertToDTO(playlist.getChapterList()));
        return playlistDTO;
    }
}
