package site.globitokuki.globitokuki_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.globitokuki.globitokuki_backend.entity.PlaylistEntity;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistEntity, Long> {
  @SuppressWarnings("null")
  public List<PlaylistEntity> findAll();
  public Optional<PlaylistEntity> findByFullName(String fullName);
  public Optional<PlaylistEntity> findByShortName(String shortName);
}
