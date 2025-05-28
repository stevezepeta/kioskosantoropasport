package kioskopasaportes.santoro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kioskopasaportes.santoro.model.FingerPrint;

@Repository
public interface FingerPrintRepository extends JpaRepository<FingerPrint, Long>{
    FingerPrint findByPersonIdPerson(Long personId);
}
