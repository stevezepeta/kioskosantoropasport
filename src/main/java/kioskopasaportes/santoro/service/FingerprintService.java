package kioskopasaportes.santoro.service;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kioskopasaportes.santoro.dto.FingerprintDataDTO;
import kioskopasaportes.santoro.model.FingerPrint;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.repository.FingerPrintRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class FingerprintService {

    private final FingerPrintRepository fingerPrintRepository;
    private final PersonRepository personRepository;

    @Transactional
    public void saveFingerprintData(FingerprintDataDTO dto, Map<String, MultipartFile> filesFingerprint) throws IOException {
        // Busca a la persona en la base de datos
        Person person = personRepository.findById(dto.getIdPerson())
                .orElseThrow(() -> new IllegalArgumentException("Person not found"));

        // Aquí deberías implementar la lógica para guardar las imágenes en disco y/o convertirlas a base64
        // Ejemplo genérico:
        String thumbLeft = fileToBase64(filesFingerprint.get("thumbLeft"));
        String thumbRight = fileToBase64(filesFingerprint.get("thumbRight"));
        String indexLeft = fileToBase64(filesFingerprint.get("indexLeft"));
        String indexRight = fileToBase64(filesFingerprint.get("indexRight"));
        String middleLeft = fileToBase64(filesFingerprint.get("middleLeft"));
        String middleRight = fileToBase64(filesFingerprint.get("middleRight"));
        String ringLeft = fileToBase64(filesFingerprint.get("ringLeft"));
        String ringRight = fileToBase64(filesFingerprint.get("ringRight"));
        String littleLeft = fileToBase64(filesFingerprint.get("littleLeft"));
        String littleRight = fileToBase64(filesFingerprint.get("littleRight"));

        // Crea y guarda el registro de huellas
      FingerPrint fingerPrint = FingerPrint.builder()
    .person(person)
    .thumbLeft(thumbLeft)
    .thumbLeftStatus(FingerPrint.fingerStatus.valueOf(dto.getThumbLeftStatus()))
    .indexLeft(indexLeft)
    .indexLeftStatus(FingerPrint.fingerStatus.valueOf(dto.getIndexLeftStatus()))
    .middleLeft(middleLeft)
    .middleLeftStatus(FingerPrint.fingerStatus.valueOf(dto.getMiddleLeftStatus()))
    .ringLeft(ringLeft)
    .ringLeftStatus(FingerPrint.fingerStatus.valueOf(dto.getRingLeftStatus()))
    .littleLeft(littleLeft)
    .littleLeftStatus(FingerPrint.fingerStatus.valueOf(dto.getLittleLeftStatus()))
    .thumbRight(thumbRight)
    .thumbRightStatus(FingerPrint.fingerStatus.valueOf(dto.getThumbRightStatus()))
    .indexRight(indexRight)
    .indexRightStatus(FingerPrint.fingerStatus.valueOf(dto.getIndexRightStatus()))
    .middleRight(middleRight)
    .middleRightStatus(FingerPrint.fingerStatus.valueOf(dto.getMiddleRightStatus()))
    .ringRight(ringRight)
    .ringRightStatus(FingerPrint.fingerStatus.valueOf(dto.getRingRightStatus()))
    .littleRight(littleRight)
    .littleRightStatus(FingerPrint.fingerStatus.valueOf(dto.getLittleRightStatus()))
    .date(LocalDateTime.now())
    .status("ACTIVE")
    .build();


        fingerPrintRepository.save(fingerPrint);

        log.info("Fingerprint data registered for person with ID: {}", person.getIdPerson());
    }

    // Utilidad simple para convertir MultipartFile a base64 (ajusta según cómo guardes tus imágenes)
    private String fileToBase64(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        return java.util.Base64.getEncoder().encodeToString(file.getBytes());
    }
}
