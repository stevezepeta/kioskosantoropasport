package kioskopasaportes.santoro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kioskopasaportes.santoro.model.FingerPrint;

public interface FingerPrintRepository extends JpaRepository<FingerPrint, Integer> {
}
