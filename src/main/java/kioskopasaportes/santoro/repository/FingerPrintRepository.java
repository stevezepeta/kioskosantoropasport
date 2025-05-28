package kioskopasaportes.santoro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kioskopasaportes.santoro.model.FingerPrint;
import kioskopasaportes.santoro.model.Person;

@Repository
public interface FingerPrintRepository extends JpaRepository<FingerPrint, Long>{
    
// Ya NO es findByPersonIdPerson, ahora es...

// ...o mejor aún, si usas Person como objeto:
Optional<FingerPrint> findByPerson(Person person);
Optional<FingerPrint> findByPersonIdPerson(Long idPerson);

}
