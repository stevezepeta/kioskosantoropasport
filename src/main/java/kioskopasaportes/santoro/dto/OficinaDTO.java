// src/main/java/kioskopasaportes/santoro/dto/OficinaDTO.java
package kioskopasaportes.santoro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OficinaDTO {
    private Integer id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String horarios;
}
