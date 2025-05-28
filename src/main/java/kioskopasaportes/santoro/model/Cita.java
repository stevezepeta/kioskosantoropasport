package kioskopasaportes.santoro.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pasaporte_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_cita_pasaporte"))
    private Pasaporte pasaporte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "oficina_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_cita_oficina"))
    private Oficina oficina;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_cita_persona"))
    private Person person;

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
