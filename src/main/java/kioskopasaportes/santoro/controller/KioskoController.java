package kioskopasaportes.santoro.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kioskopasaportes.santoro.dto.EstadoDTO;
import kioskopasaportes.santoro.dto.EstadoMunicipioDTO;
import kioskopasaportes.santoro.dto.FingerprintResultDTO;
import kioskopasaportes.santoro.model.FingerPrint;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.model.Pasaporte;
import kioskopasaportes.santoro.model.dto.FingerprintVerificationResponseDTO;
import kioskopasaportes.santoro.repository.FingerPrintRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import kioskopasaportes.santoro.repository.PasaporteRepository;
import kioskopasaportes.santoro.service.EstadoMunicipioService;
import kioskopasaportes.santoro.service.FingerprintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/fingerprint")
@RequiredArgsConstructor
@Slf4j
public class KioskoController {

    private final PersonRepository personRepository;
    private final FingerPrintRepository fingerPrintRepository;
    private final PasaporteRepository pasaporteRepository;
    private final FingerprintService fingerprintService;
    private final EstadoMunicipioService estadoMunicipioService;

    @PostMapping("/verify")
    public ResponseEntity<FingerprintVerificationResponseDTO> verifyFingerprint(
            @RequestParam("curp") String curp,
            @RequestParam Map<String, MultipartFile> filesBiometric,
            @RequestParam(value = "facePhoto", required = false) MultipartFile facePhoto
    ) {
        log.info("=== [POST /api/fingerprint/verify] ===");
        log.info("CURP: {}", curp);
        log.info("filesBiometric keys: {}", filesBiometric.keySet());
        log.info("facePhoto: {}", (facePhoto != null) ? facePhoto.getOriginalFilename() : "null");

        try {
            // Buscar persona por CURP
            Person person = personRepository.findByCurp(curp).orElse(null);
            if (person == null) {
                log.warn("Persona NO encontrada para CURP: {}", curp);
                return ResponseEntity.badRequest().build();
            }
            log.info("Persona encontrada: {} {}", person.getNombres(), person.getApellidos());

            // Buscar la huella registrada para la persona
            FingerPrint fingerPrint = fingerPrintRepository.findByPerson(person).orElse(null);
            if (fingerPrint == null) {
                log.warn("Huella NO encontrada para persona con CURP: {}", curp);
                return ResponseEntity.notFound().build();
            }
            log.info("Registro de huella encontrado para persona: {}", curp);

            // Lista de dedos estándar
            String[] fingerKeys = {
                "thumbLeft", "indexLeft", "middleLeft", "ringLeft", "littleLeft",
                "thumbRight", "indexRight", "middleRight", "ringRight", "littleRight"
            };

            boolean atLeastOneFingerPresent = false;
            boolean matchFound = false;
            String matchedFinger = null;
            FingerprintResultDTO matchResult = null;

            for (String finger : fingerKeys) {
                MultipartFile file = filesBiometric.get(finger);
                String savedFingerprintPath = getFingerprintPath(fingerPrint, finger);
                if (file != null && !file.isEmpty() && savedFingerprintPath != null) {
                    atLeastOneFingerPresent = true;

                    String uploadedFingerprintBase64 = Base64.getEncoder().encodeToString(file.getBytes());

                    FingerprintResultDTO result = fingerprintService.compareFingerprints(
                            uploadedFingerprintBase64, savedFingerprintPath, true
                    );

                    log.info("Comparando dedo {}: resultado={}", finger, result);

                    if (result != null && result.isMatch()) {
                        matchFound = true;
                        matchedFinger = finger;
                        matchResult = result;
                        break;
                    }
                }
            }

            if (!atLeastOneFingerPresent) {
                log.warn("No se recibió ninguna huella digital para verificar.");
                return ResponseEntity.badRequest().build();
            }

            if (!matchFound) {
                log.warn("NO hubo match entre huellas para CURP {} en ninguno de los 10 dedos", curp);
                return ResponseEntity.noContent().build();
            }

            log.info("¡MATCH exitoso! Dedo: {} - Score: {}, Porcentaje: {}", matchedFinger, matchResult.getScore(), matchResult.getPercentage());

            // GUARDAR/ACTUALIZAR LA FOTO FACIAL si existe
            String facePhotoPath = person.getFacePhoto();
            if (facePhoto != null && !facePhoto.isEmpty()) {
                String baseDir = "C:/Users/Windows/Pictures/huellaskiosko/Cara";
                String userDir = baseDir + "/" + curp;
                File dir = new File(userDir);
                if (!dir.exists()) dir.mkdirs();

                if (facePhotoPath != null && !facePhotoPath.isBlank()) {
                    File prevFile = new File(facePhotoPath);
                    if (prevFile.exists()) prevFile.delete();
                }

                String filename = "face_" + System.currentTimeMillis() + ".jpg";
                String path = userDir + File.separator + filename;

                try (FileOutputStream fos = new FileOutputStream(path)) {
                    fos.write(facePhoto.getBytes());
                }

                facePhotoPath = path;
                person.setFacePhoto(facePhotoPath);
                personRepository.save(person);
                log.info("Foto facial guardada/actualizada en: {}", facePhotoPath);
            }

            // Buscar pasaporte asociado a la persona
            Pasaporte pasaporte = pasaporteRepository.findByPersona(person).orElse(null);

            // === Buscar estado como objeto con id y nombre ===
            EstadoDTO estadoDTO = null;
            if (person.getEstado() != null && !person.getEstado().isBlank()) {
                Optional<EstadoMunicipioDTO> estadoOpt = estadoMunicipioService.getEstadoByNombre(person.getEstado());
                if (estadoOpt.isPresent()) {
                    EstadoMunicipioDTO estadoCat = estadoOpt.get();
                    estadoDTO = new EstadoDTO(estadoCat.getId(), estadoCat.getNombre());
                }
            }

            // Prepara la respuesta del DTO
            FingerprintVerificationResponseDTO response = new FingerprintVerificationResponseDTO();
            response.setIdPerson(person.getIdPerson());
            response.setCurp(person.getCurp());
            response.setNombres(person.getNombres());
            response.setApellidos(person.getApellidos());
            response.setFechaNacimiento(person.getFechaNacimiento());
            response.setSexo(person.getSexo());
            response.setNacionalidad(person.getNacionalidad());
            response.setLugarNacimiento(person.getLugarNacimiento());
            response.setFacePhoto(facePhotoPath);
            response.setEstado(estadoDTO); // <<--- Estado como objeto

            // Datos del pasaporte (si existe)
            if (pasaporte != null) {
                response.setNumeroPasaporte(pasaporte.getNumeroPasaporte());
                response.setTipoDocumento(pasaporte.getTipoDocumento());
                response.setCodigoPais(pasaporte.getCodigoPais());
                response.setLugarEmision(pasaporte.getLugarEmision());
                response.setAutoridad(pasaporte.getAutoridad());
                response.setFechaEmision(pasaporte.getFechaEmision());
                response.setFechaExpiracion(pasaporte.getFechaExpiracion());
                response.setMrz(pasaporte.getMrz());
            }

            log.info("Enviando respuesta: {}", response);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Error comparando huellas dactilares: ", ex);
            return ResponseEntity.internalServerError().build();
        }
    }

    // Utilitario: obtiene la ruta del archivo guardado para cada dedo
    private String getFingerprintPath(FingerPrint fingerPrint, String finger) {
        switch (finger) {
            case "thumbLeft": return fingerPrint.getThumbLeft();
            case "indexLeft": return fingerPrint.getIndexLeft();
            case "middleLeft": return fingerPrint.getMiddleLeft();
            case "ringLeft": return fingerPrint.getRingLeft();
            case "littleLeft": return fingerPrint.getLittleLeft();
            case "thumbRight": return fingerPrint.getThumbRight();
            case "indexRight": return fingerPrint.getIndexRight();
            case "middleRight": return fingerPrint.getMiddleRight();
            case "ringRight": return fingerPrint.getRingRight();
            case "littleRight": return fingerPrint.getLittleRight();
            default: return null;
        }
    }
}
