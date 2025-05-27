package kioskopasaportes.santoro.repository;

import kioskopasaportes.santoro.model.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByFechaCita(LocalDate fecha);
}
