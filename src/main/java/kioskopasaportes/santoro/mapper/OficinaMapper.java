package kioskopasaportes.santoro.mapper;

import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.model.Oficina;
import org.mapstruct.*;

/**
 * Mapper entre la entidad {@link Oficina} y su DTO {@link OficinaDTO}.
 * <p>
 * Se genera automáticamente una implementación en
 * <code>target/generated-sources/annotations</code> cuando compilas
 * el proyecto (MapStruct + annotation processor).
 */
@Mapper(componentModel = "spring")
public interface OficinaMapper {

    /* ---------- DTO → Entidad ---------- */
    Oficina toEntity(OficinaDTO dto);

    /* ---------- Entidad → DTO ---------- */
    OficinaDTO toDto(Oficina entity);

    /* ---------- Listados ---------- */
    java.util.List<OficinaDTO> toDto(java.util.List<Oficina> entities);

    /**
     * Actualiza una entidad existente con los valores no nulos del DTO.
     * Los campos <code>null</code> se ignoran para no sobreescribir datos.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(OficinaDTO dto, @MappingTarget Oficina entity);
}
