package kioskopasaportes.santoro.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;

@Entity
@Table(name = "oficinas")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Oficina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, nullable = false)
    private String nombre;

    @Column(length = 160, nullable = false)
    private String direccion;

    /**
     * Estructura JSON con las franjas horarias de atención.
     * Ejemplo:
     * {
     *   "lu": ["08:00-14:00"],
     *   "ma": ["08:00-14:00"],
     *   ...
     * }
     */
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String horario;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;
}
