package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;
import java.util.List;

import kioskopasaportes.santoro.dto.EstadoDTO;
import kioskopasaportes.santoro.dto.MunicipioDTO;
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
    private EstadoDTO estado;
    private MunicipioDTO municipio;
    private String facePhoto;
    private List<MunicipioDTO> municipios;
    // Resultado biométrico
    // private boolean match;
    // private BigDecimal score;
    // private BigDecimal percentage;
}
