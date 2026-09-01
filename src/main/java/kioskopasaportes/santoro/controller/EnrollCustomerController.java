package kioskopasaportes.santoro.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kioskopasaportes.santoro.dto.EnrollBiometricDataDTO;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.model.dto.EnrollPersonDTO;
import kioskopasaportes.santoro.model.dto.PersonEnrolledDTO;
import kioskopasaportes.santoro.repository.PersonRepository;
import kioskopasaportes.santoro.rulesException.EnrollException;
import kioskopasaportes.santoro.rulesException.ModelNotFoundException;
import kioskopasaportes.santoro.service.EnrollCustomerService;
import kioskopasaportes.santoro.util.Message;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollCustomer")
public class EnrollCustomerController {
    private final ObjectMapper objectMapper;
    private final EnrollCustomerService enrollCustomerService;
private final PersonRepository personRepository;



// ...
@PostMapping(value = "/enroll/biographic", consumes = "multipart/form-data")
public ResponseEntity<Message> enrollBiographic(
    @RequestParam("curp") String curp,
    @RequestParam("nombres") String nombres,
    @RequestParam("primerApellido") String primerApellido,
    @RequestParam("segundoApellido") String segundoApellido,
    @RequestParam("fechaNacimiento") String fechaNacimientoStr,
    @RequestParam("sexo") String sexo,
    @RequestParam("nacionalidad") String nacionalidad,
    @RequestParam("direccion") String direccion,
    @RequestParam("estado") Integer estado,
    @RequestParam("municipio") Integer municipio,
    @RequestParam(value = "facePhoto", required = false) MultipartFile facePhoto
) {
    // Construye el DTO
    EnrollPersonDTO enrollPersonDTO = new EnrollPersonDTO();
    enrollPersonDTO.setCurp(curp);
    enrollPersonDTO.setNombres(nombres);
    enrollPersonDTO.setPrimerApellido(primerApellido);
    enrollPersonDTO.setSegundoApellido(segundoApellido);
    enrollPersonDTO.setFechaNacimiento(LocalDate.parse(fechaNacimientoStr));
    enrollPersonDTO.setSexo(sexo);
    enrollPersonDTO.setNacionalidad(nacionalidad);
    enrollPersonDTO.setDireccion(direccion);
    enrollPersonDTO.setEstado(estado);
    enrollPersonDTO.setMunicipio(municipio);

    // Guarda la persona en la base de datos
    Person personSaved = enrollCustomerService.enrollBiographic(enrollPersonDTO);

    // Guarda la foto si se envía
    if (facePhoto != null && !facePhoto.isEmpty()) {
        String facePath = saveFacePhoto(facePhoto, personSaved.getCurp());
        personSaved.setFacePhoto(facePath);
        personRepository.save(personSaved);
    }

    // Construye la respuesta DTO (ajusta el constructor si es necesario)
    PersonEnrolledDTO personEnrolled = new PersonEnrolledDTO(
        personSaved.getIdPerson(),
        personSaved.getNombres() + " " + personSaved.getApellidos()
    );

    return ResponseEntity.ok(new Message(true, "Datos biográficos enrolados correctamente", personEnrolled));
}

// === MÉTODO PARA GUARDAR LA FOTO FACIAL ===
private String saveFacePhoto(MultipartFile facePhoto, String curp) {
    try {
        String baseDir = "C:/Users/Windows/Pictures/huellaskiosko/Cara";
        String userDir = baseDir + "/" + curp;
        File dir = new File(userDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = "face_" + System.currentTimeMillis() + ".jpg";
        String path = userDir + File.separator + filename;

        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(facePhoto.getBytes());
        }
        return path;
    } catch (Exception e) {
        throw new RuntimeException("Error al guardar la foto facial", e);
    }
}

   
    @PostMapping(value = "/enroll/fingerprint")
    public ResponseEntity<Message> enrollBiometric(@RequestParam Map<String, MultipartFile> filesBiometric, @RequestParam("info") @NotNull @NotBlank String info) throws IOException, EnrollException, ModelNotFoundException {
           EnrollBiometricDataDTO enrollCustomerDataDTO = objectMapper.readValue(info, EnrollBiometricDataDTO.class);
        enrollCustomerService.enrollBiometric(enrollCustomerDataDTO, filesBiometric);
        return ResponseEntity.ok(new Message(true, "Biometric data enrolled successfully", null));
    }

  


}
