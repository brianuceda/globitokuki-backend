package site.globitokuki.globitokuki_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import site.globitokuki.globitokuki_backend.entities.SessionEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Long> {
  Optional<SessionEntity> findByDomain(String name);
  Optional<SessionEntity> findByNameAndDomain(String name, String domain);
}
