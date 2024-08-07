package site.globitokuki.globitokuki_backend.dtos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.globitokuki.globitokuki_backend.entities.ChapterEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChapterDTO {

  private Integer chapterNumber;
  private String ytId;
  
  private String viewedTime;
  private String totalTime;
  
  private Boolean havePreviousChapter;
  private Boolean haveNextChapter;

  public ChapterEntity convertToEntity() {
    ChapterEntity chapterEntity = new ChapterEntity();
    chapterEntity.setChapterNumber(this.chapterNumber);
    chapterEntity.setYtId(this.ytId);
    chapterEntity.setViewedTime(this.viewedTime);
    chapterEntity.setTotalTime(this.totalTime);
    chapterEntity.setHavePreviousChapter(this.havePreviousChapter);
    chapterEntity.setHaveNextChapter(this.haveNextChapter);
    return chapterEntity;
  }

  public static List<ChapterDTO> convertEntityListToDTOList(List<ChapterEntity> chapters) {
    List<ChapterDTO> chapterDTOs = new ArrayList<>();
    
    for (ChapterEntity chapter : chapters) {
      ChapterDTO chapterDTO = new ChapterDTO();
      chapterDTO.setChapterNumber(chapter.getChapterNumber());
      chapterDTO.setYtId(chapter.getYtId());
      chapterDTO.setViewedTime(chapter.getViewedTime());
      chapterDTO.setTotalTime(chapter.getTotalTime());
      chapterDTO.setHavePreviousChapter(chapter.getHavePreviousChapter());
      chapterDTO.setHaveNextChapter(chapter.getHaveNextChapter());
      chapterDTOs.add(chapterDTO);
    }
    
    chapterDTOs.sort((c1, c2) -> c1.getChapterNumber().compareTo(c2.getChapterNumber()));

    return chapterDTOs;
  }
}
