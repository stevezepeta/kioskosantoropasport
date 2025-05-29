package kioskopasaportes.santoro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "citas")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ciudadano_id_externo", nullable = false)
    private Long ciudadanoIdExterno;

    // ID de la oficina seleccionada
    @Column(name = "oficina_id", nullable = false)
    private Integer oficinaId;

    // Nombre de la oficina seleccionada
    @Column(name = "oficina_nombre", length = 150)
    private String oficinaNombre;

    // Relación a la persona que agenda la cita (solo ID)
    @Column(name = "person_id", nullable = false)
    private Long personId;

    @Column(name = "fecha_cita", nullable = false)
    private LocalDate fechaCita;

    @Column(name = "hora_cita", nullable = false)
    private LocalTime horaCita;

    @Lob
    @Column(name = "codigo_qr")
    private byte[] codigoQr;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
}
