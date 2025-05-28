package kioskopasaportes.santoro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kioskopasaportes.santoro.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
