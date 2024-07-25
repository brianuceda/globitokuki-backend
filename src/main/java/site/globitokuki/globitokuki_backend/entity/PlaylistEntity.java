package site.globitokuki.globitokuki_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private String startViewingDate;
  private String endViewingDate;
  private String playlistLink;
  private String thumbnail;
  
  @OneToMany(mappedBy = "playlist", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ChapterEntity> chapterList;

  // Método para contar los capítulos
  public int getChapters() {
    return this.chapterList != null ? this.chapterList.size() : 0;
  }
}
