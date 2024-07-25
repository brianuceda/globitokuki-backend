package site.globitokuki.globitokuki_backend.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.globitokuki.globitokuki_backend.entity.ChapterEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterDTO {
  private Integer chapterNumber;
  private String ytId;

  public ChapterEntity convertToEntity() {
    ChapterEntity chapterEntity = new ChapterEntity();
    chapterEntity.setChapterNumber(this.chapterNumber);
    chapterEntity.setYtId(this.ytId);
    return chapterEntity;
  }

  public static List<ChapterEntity> convertToEntityList(List<ChapterDTO> chapterDTOs) {
    List<ChapterEntity> chapterEntities = new ArrayList<>();
    for (ChapterDTO chapterDTO : chapterDTOs) {
      chapterEntities.add(chapterDTO.convertToEntity());
    }
    return chapterEntities;
  }

  public static List<ChapterDTO> convertToDTO(List<ChapterEntity> chapters) {
    List<ChapterDTO> chapterDTOs = new ArrayList<>();
    for (ChapterEntity chapter : chapters) {
      ChapterDTO chapterDTO = new ChapterDTO();
      chapterDTO.setChapterNumber(chapter.getChapterNumber());
      chapterDTO.setYtId(chapter.getYtId());
      chapterDTOs.add(chapterDTO);
    }
    return chapterDTOs;
  }
}
