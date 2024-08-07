package site.globitokuki.globitokuki_backend.entities;

import jakarta.persistence.*;
import lombok.*;
import site.globitokuki.globitokuki_backend.entities.enums.StatesEnum;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "playlist")
public class PlaylistEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String realName;

  @Column(unique = true, nullable = false)
  private String fullName;

  @Column(unique = true, nullable = false)
  private String shortName;

  private Integer orderView;
  
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "image_entity_id", referencedColumnName = "id")
  private ImageEntity thumbnail;

  private String startViewingDate;
  private String endViewingDate;

  @Column(columnDefinition = "TEXT")
  private String description;

  private String playlistLink;
  
  private String ourComment;
  private Double starsGlobito;
  private Double starsKuki;

  @Enumerated(EnumType.STRING)
  private StatesEnum state;

  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChapterEntity> chapterList;
  
  private Integer lastChapterViewed;
  @Column(length = 8)
  private String lastChapterViewedTime;
  @Column(length = 8)
  private String lastChapterTotalTime;

  public int getChapters() {
    return this.chapterList != null ? this.chapterList.size() : 0;
  }
}
