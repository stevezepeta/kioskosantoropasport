package kioskopasaportes.santoro.mapper;

import kioskopasaportes.santoro.dto.CitaDTO;
import kioskopasaportes.santoro.model.Cita;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    /* DTO → Entidad */
    @Mapping(source = "pasaporteId", target = "pasaporte.id")
    @Mapping(source = "oficinaId",   target = "oficina.id")
    Cita toEntity(CitaDTO dto);

    /* Entidad → DTO */
    @Mapping(source = "pasaporte.id", target = "pasaporteId")
    @Mapping(source = "oficina.id",   target = "oficinaId")
    CitaDTO toDto(Cita entity);

    List<CitaDTO> toDto(List<Cita> entities);

    /* Actualizar entidad existente ignorando nulos */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "pasaporteId", target = "pasaporte.id")
    @Mapping(source = "oficinaId",   target = "oficina.id")
    void updateEntity(CitaDTO dto, @MappingTarget Cita entity);
}
