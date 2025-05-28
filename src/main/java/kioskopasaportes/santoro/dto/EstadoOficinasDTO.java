// src/main/java/kioskopasaportes/santoro/dto/EstadoOficinasDTO.java
package kioskopasaportes.santoro.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EstadoOficinasDTO {
    private Integer id;
    private String nombre;
    private List<OficinaDTO> oficinas;
}
