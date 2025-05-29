package kioskopasaportes.santoro.dto;

import lombok.Data;

@Data
public class CitaRequestDTO {
    private Integer estadoId;
    private Integer oficinaId;
    private String fecha; // yyyy-MM-dd
    private String hora;  // HH:mm
}
