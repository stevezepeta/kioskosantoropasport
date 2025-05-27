package kioskopasaportes.santoro.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CitaDTO {
    private Long id;
    private Long ciudadanoIdExterno;
    private Long pasaporteId;
    private Long oficinaId;
    private LocalDate fechaCita;
    private LocalTime horaCita;
    private byte[] codigoQr;
}
