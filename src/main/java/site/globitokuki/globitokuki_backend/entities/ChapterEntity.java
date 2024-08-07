package site.globitokuki.globitokuki_backend.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chapter")
public class ChapterEntity {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private Integer chapterNumber;
  private String ytId;
  
  @Column(length = 8)
  private String viewedTime;
  @Column(length = 8)
  private String totalTime;

  private Boolean havePreviousChapter;
  private Boolean haveNextChapter;
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "playlist_id")
  private PlaylistEntity playlist;
}
