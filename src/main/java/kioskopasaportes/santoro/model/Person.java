package kioskopasaportes.santoro.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persons")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Person {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_person")  // <--- Esto es clave
private Long idPerson;

    

    @Column(length = 225, nullable = false, unique = true)
    private String curp;

    @Column(length = 100, nullable = false)
    private String nombres;

    @Column(length = 100, nullable = false)
    private String primerApellido;

    @Column(length = 100)
    private String segundoApellido;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 1, nullable = false)
    private String sexo;

    @Column(length = 100)
    private String nacionalidad;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String estado;

    @Column(length = 100)
    private String municipio;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pasaporte> pasaportes;
    
    @Column(length = 500)
    private String facePhoto; 

}
