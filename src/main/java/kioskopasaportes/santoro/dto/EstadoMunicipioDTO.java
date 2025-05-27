package kioskopasaportes.santoro.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa un Estado con su lista de municipios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadoMunicipioDTO {

    private Integer id;                // id del estado
    private String  nombre;            // nombre del estado
    private List<MunicipioDTO> municipios; // lista de municipios pertenecientes al estado

    /**
     * DTO interno (o puede ir en su propio archivo) que representa un municipio.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MunicipioDTO {
        private Integer id;     // id del municipio
        private String  nombre; // nombre del municipio
    }
}
