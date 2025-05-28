package kioskopasaportes.santoro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kioskopasaportes.santoro.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByCurp(String curp);

}
