// Crea una clase utilitaria para cargar el catálogo solo una vez
package kioskopasaportes.santoro.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kioskopasaportes.santoro.dto.EstadoDTO;
import org.springframework.core.io.ClassPathResource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class CatalogoEstadosMunicipios {

    private static List<EstadoDTO> estados;

    public static List<EstadoDTO> getCatalogo() {
        if (estados == null) {
            cargarCatalogo();
        }
        return estados;
    }

    private static void cargarCatalogo() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("estados_municipios.json").getInputStream()) {
            estados = mapper.readValue(is, new TypeReference<List<EstadoDTO>>() {});
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el catálogo de estados y municipios", e);
        }
    }
}
