package kioskopasaportes.santoro.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.repository.PersonRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    /**
     * Busca una persona por su ID.
     * @param idPerson El ID de la persona.
     * @return La persona encontrada, o null si no existe.
     */
    public Person findById(Long idPerson) {
        Optional<Person> personOpt = personRepository.findById(idPerson);
        return personOpt.orElse(null);
    }

    // Puedes agregar aquí otros métodos según tus necesidades:
    // - findByCurp(String curp)
    // - save(Person person)
    // - update(Person person)
    // - delete(Long id)
}
