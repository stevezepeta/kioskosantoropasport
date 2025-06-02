
package kioskopasaportes.santoro.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Column(name = "id_person")
    private Long idPerson;

    @Column(length = 100, nullable = false)
    private String apellidos;

    @Column(length = 100, nullable = false)
    private String nombres;

    @Column(length = 1, nullable = false)
    private String sexo;

    @Column(length = 100, nullable = false)
    private String nacionalidad;

    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 150)
    private String lugarNacimiento;

    @Column(length = 225, nullable = true, unique = false)
    private String curp;

    @Column(length = 500)
    private String facePhoto; // Base64 o URL

    @Column(length = 500)
    private String firma; // (opcional, imagen escaneada de la firma)



    @Column(length = 100)
    private String estado;

    @Column(length = 100)
    private String municipio;

 
}  


