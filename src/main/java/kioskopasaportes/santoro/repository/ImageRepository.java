package kioskopasaportes.santoro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kioskopasaportes.santoro.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
