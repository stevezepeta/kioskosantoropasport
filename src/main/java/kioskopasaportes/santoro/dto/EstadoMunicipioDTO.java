// kioskopasaportes.santoro.dto.EstadoMunicipioDTO.java
package kioskopasaportes.santoro.dto;
import java.util.List;

import lombok.Data;

@Data
public class EstadoMunicipioDTO {
    private Integer id;
    private String nombre;
    private List<MunicipioDTO> municipios; // OJO: Referencia al DTO global
}
