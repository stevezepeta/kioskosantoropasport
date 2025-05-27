package kioskopasaportes.santoro.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Data
public class PasaporteDTO {
    private Long id;
    private Long ciudadanoIdExterno;
    private String numeroPasaporte;
    private LocalDate fechaSolicitud;
    private String lugarEmision;
    private LocalDate fechaEmision;
    private LocalDate fechaExpiracion;
    private LocalDate fechaEntrega;
    private Boolean esRobo;
    private String numeroConstanciaRobo;
    private OffsetDateTime creadoEn;
}
