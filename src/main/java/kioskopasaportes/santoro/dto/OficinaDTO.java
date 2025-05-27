package kioskopasaportes.santoro.dto;

import lombok.Data;

@Data
public class OficinaDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String horario; // JSON textual
}
