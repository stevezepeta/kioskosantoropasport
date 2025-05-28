package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FingerprintVerificationResponseDTO {
    // Datos de la persona
    private Long idPerson;
    private String curp;
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String nacionalidad;
    private String direccion;
    private String estado;
    private String municipio;
    private String facePhoto;
    // Resultado biométrico
    // private boolean match;
    // private BigDecimal score;
    // private BigDecimal percentage;
}
