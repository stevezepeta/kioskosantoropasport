package kioskopasaportes.santoro.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "pasaportes",
    uniqueConstraints = @UniqueConstraint(name = "uk_pasaporte_numero", columnNames = "numero_pasaporte")
)
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Pasaporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_pasaporte_persona"))
    private Person person;

    @Column(name = "numero_pasaporte", length = 10, nullable = false)
    private String numeroPasaporte;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;

    @Column(name = "lugar_emision", length = 80, nullable = false)
    private String lugarEmision;

    @Column(name = "fecha_emision")
    private LocalDate fechaEmision;

    @Column(name = "fecha_expiracion")
    private LocalDate fechaExpiracion;

    @Column(name = "fecha_entrega")
    private LocalDate fechaEntrega;

    @Builder.Default
    @Column(name = "es_robo", nullable = false)
    private Boolean esRobo = false;

    @Column(name = "numero_constancia_robo", length = 45)
    private String numeroConstanciaRobo;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @OneToMany(mappedBy = "pasaporte", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;
}
