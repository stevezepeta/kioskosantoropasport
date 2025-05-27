package kioskopasaportes.santoro.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

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

    /* Relaciones */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pasaporte_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_cita_pasaporte"))
    private Pasaporte pasaporte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oficina_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_cita_oficina"))
    private Oficina oficina;

    /* Datos de la cita */
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
