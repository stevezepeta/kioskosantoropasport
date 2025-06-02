package kioskopasaportes.santoro.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    // Relación con Person
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Person persona;

    @Column(name = "numero_pasaporte", length = 15, nullable = false)
    private String numeroPasaporte;

    @Column(name = "tipo_documento", length = 3, nullable = false)
    private String tipoDocumento; // "P"

    @Column(name = "codigo_pais", length = 3, nullable = false)
    private String codigoPais; // "MEX"

    @Column(name = "lugar_emision", length = 80, nullable = false)
    private String lugarEmision; // Ej. "TOLUCA, MEXICO"

    @Column(name = "autoridad", length = 150, nullable = false)
    private String autoridad; // "SECRETARIA DE RELACIONES EXTERIORES"

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDate fechaExpiracion;

    @Column(name = "mrz", length = 128)
    private String mrz; // Línea MRZ (zona legible por máquina)

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
}
