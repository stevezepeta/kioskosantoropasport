package kioskopasaportes.santoro.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EnrollPersonDTO {
    private String curp;
    private String nombres;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String nacionalidad;
    private String lugarNacimiento;    // <--- Agregado para sincronizar con tu modelo
     private Integer estado;    // <--- Cambia a Integer (ID del estado)
    private Integer municipio; // <--- Cambia a Integer (ID del municipio)
    private String direccion;          // (No está en el modelo, pero si la usas en lógica, déjala)
    

    // Método auxiliar para combinar apellidos
    public String getApellidos() {
        StringBuilder sb = new StringBuilder();
        if (primerApellido != null && !primerApellido.isBlank()) {
            sb.append(primerApellido);
        }
        if (segundoApellido != null && !segundoApellido.isBlank()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(segundoApellido);
        }
        return sb.toString().trim();
    }
}
