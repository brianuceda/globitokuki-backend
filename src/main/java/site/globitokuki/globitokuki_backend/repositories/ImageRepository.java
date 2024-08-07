package site.globitokuki.globitokuki_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.globitokuki.globitokuki_backend.entities.ImageEntity;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {
}