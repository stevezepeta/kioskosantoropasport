package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;

import kioskopasaportes.santoro.dto.EstadoDTO; // <--- IMPORTANTE
import lombok.Data;

@Data
public class FingerprintVerificationResponseDTO {
    private Long idPerson;
    private String curp;
    private String nombres;
    private String apellidos;
    private String sexo;
    private String nacionalidad;
    private LocalDate fechaNacimiento;
    private String lugarNacimiento;
    private String facePhoto;

    private EstadoDTO estado; // <--- AGREGA ESTE CAMPO

    // Datos del pasaporte
    private String numeroPasaporte;
    private String tipoDocumento;
    private String codigoPais;
    private String lugarEmision;
    private String autoridad;
    private LocalDate fechaEmision;
    private LocalDate fechaExpiracion;
    private String mrz;
}
