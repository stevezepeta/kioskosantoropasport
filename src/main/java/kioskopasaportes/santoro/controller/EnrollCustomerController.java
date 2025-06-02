package kioskopasaportes.santoro.controller;

import java.io.IOException;
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


   @PostMapping(value = "/enroll/biographic")
public ResponseEntity<Message> enrollBiographic(@RequestBody @Valid EnrollPersonDTO enrollPersonDTO) {
    Person personSaved = enrollCustomerService.enrollBiographic(enrollPersonDTO);
   PersonEnrolledDTO personEnrolled = PersonEnrolledDTO.builder()
    .idPerson(personSaved.getIdPerson())
    .nombreCompleto(
        personSaved.getNombres() + " " + personSaved.getApellidos()
    )
    .build();

    return ResponseEntity.ok(new Message(true, "Datos biográficos enrolados correctamente", personEnrolled));
}

   
    @PostMapping(value = "/enroll/fingerprint")
    public ResponseEntity<Message> enrollBiometric(@RequestParam Map<String, MultipartFile> filesBiometric, @RequestParam("info") @NotNull @NotBlank String info) throws IOException, EnrollException, ModelNotFoundException {
           EnrollBiometricDataDTO enrollCustomerDataDTO = objectMapper.readValue(info, EnrollBiometricDataDTO.class);
        enrollCustomerService.enrollBiometric(enrollCustomerDataDTO, filesBiometric);
        return ResponseEntity.ok(new Message(true, "Biometric data enrolled successfully", null));
    }

  


}
