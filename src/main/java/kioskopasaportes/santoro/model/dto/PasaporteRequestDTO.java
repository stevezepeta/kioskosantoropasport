package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PasaporteRequestDTO {
    private String curp;
    private String numeroPasaporte;
    private String tipoDocumento;
    private String codigoPais;
    private String lugarEmision;
    private String autoridad;
    private LocalDate fechaEmision;
    private LocalDate fechaExpiracion;
    private String mrz;
}
