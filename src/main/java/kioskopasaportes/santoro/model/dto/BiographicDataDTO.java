package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiographicDataDTO {

    private Long idPerson; // solo para casos de edición

    @NotBlank(message = "CURP is required")
    @Size(min = 18, max = 18, message = "CURP must be 18 characters")
    private String curp;

    @NotBlank(message = "First name(s) is required")
    @Size(max = 100)
    private String nombres;

    @NotBlank(message = "Primer apellido is required")
    @Size(max = 100)
    private String primerApellido;

    @Size(max = 100)
    private String segundoApellido;

    @NotNull(message = "Fecha de nacimiento is required")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "Sexo is required")
    @Pattern(regexp = "[HM]", message = "Sexo must be H (hombre) or M (mujer)")
    private String sexo;

    @NotBlank(message = "Nacionalidad is required")
    @Size(max = 100)
    private String nacionalidad;

    @NotBlank(message = "Dirección is required")
    @Size(max = 255)
    private String direccion;

    @NotBlank(message = "Estado is required")
    @Size(max = 100)
    private String estado;

    @NotBlank(message = "Municipio is required")
    @Size(max = 100)
    private String municipio;
}
