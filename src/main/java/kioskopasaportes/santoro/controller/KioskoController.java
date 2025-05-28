package kioskopasaportes.santoro.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import kioskopasaportes.santoro.dto.FingerprintResultDTO;
import kioskopasaportes.santoro.model.FingerPrint;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.model.dto.FingerprintVerificationResponseDTO;
import kioskopasaportes.santoro.repository.FingerPrintRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
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
    private final FingerprintService fingerprintService;

    @PostMapping("/verify")
    public ResponseEntity<FingerprintVerificationResponseDTO> verifyFingerprint(
            @RequestParam("curp") String curp,
            @RequestParam("fingerType") String fingerType,
            @RequestParam("fingerImage") MultipartFile fingerImage,
            @RequestParam(value = "facePhoto", required = false) MultipartFile facePhoto
    ) {
        log.info("=== [POST /api/fingerprint/verify] ===");
        log.info("CURP: {}", curp);
        log.info("fingerType: {}", fingerType);
        log.info("fingerImage original filename: {}", fingerImage != null ? fingerImage.getOriginalFilename() : "null");
        log.info("facePhoto: {}", (facePhoto != null) ? facePhoto.getOriginalFilename() : "null");

        try {
            // Buscar persona por CURP
            Person person = personRepository.findByCurp(curp).orElse(null);
            if (person == null) {
                log.warn("Persona NO encontrada para CURP: {}", curp);
                return ResponseEntity.badRequest().build();
            }
            log.info("Persona encontrada: {} {}", person.getNombres(), person.getPrimerApellido());

            // Buscar la huella registrada para la persona
            FingerPrint fingerPrint = fingerPrintRepository.findByPerson(person).orElse(null);
            if (fingerPrint == null) {
                log.warn("Huella NO encontrada para persona con CURP: {}", curp);
                return ResponseEntity.notFound().build();
            }
            log.info("Registro de huella encontrado para persona: {}", curp);

            // Obtener la ruta del archivo de huella para el dedo solicitado
            String savedFingerprintPath;
            switch (fingerType) {
                case "thumbRight": savedFingerprintPath = fingerPrint.getThumbRight(); break;
                case "thumbLeft": savedFingerprintPath = fingerPrint.getThumbLeft(); break;
                case "indexRight": savedFingerprintPath = fingerPrint.getIndexRight(); break;
                case "indexLeft": savedFingerprintPath = fingerPrint.getIndexLeft(); break;
                case "middleRight": savedFingerprintPath = fingerPrint.getMiddleRight(); break;
                case "middleLeft": savedFingerprintPath = fingerPrint.getMiddleLeft(); break;
                case "ringRight": savedFingerprintPath = fingerPrint.getRingRight(); break;
                case "ringLeft": savedFingerprintPath = fingerPrint.getRingLeft(); break;
                case "littleRight": savedFingerprintPath = fingerPrint.getLittleRight(); break;
                case "littleLeft": savedFingerprintPath = fingerPrint.getLittleLeft(); break;
                default:
                    log.warn("fingerType no válido: {}", fingerType);
                    return ResponseEntity.badRequest().build();
            }

            if (savedFingerprintPath == null) {
                log.warn("No existe ruta de archivo para el tipo de dedo {} en la persona con CURP: {}", fingerType, curp);
                return ResponseEntity.status(404).body(null);
            }
            log.info("Ruta de archivo registrada para {} obtenida correctamente: {}", fingerType, savedFingerprintPath);

            // Convertir la imagen recibida a Base64
            String uploadedFingerprintBase64 = Base64.getEncoder().encodeToString(fingerImage.getBytes());
            log.info("Imagen de huella recibida convertida a base64 (tamaño {} bytes).", uploadedFingerprintBase64.length());

            // Comparar las huellas
            log.info("Comparando huella enviada con la registrada...");
            FingerprintResultDTO result = fingerprintService.compareFingerprints(
                    uploadedFingerprintBase64, savedFingerprintPath, true
            );

            log.info("Resultado comparación: {}", result);

            if (result == null || !result.isMatch()) {
                log.warn("NO hubo match entre huellas para CURP {} y dedo {}", curp, fingerType);
                return ResponseEntity.noContent().build(); // No match
            }

            log.info("¡MATCH exitoso! Score: {}, Porcentaje: {}", result.getScore(), result.getPercentage());

            // GUARDAR/ACTUALIZAR LA FOTO FACIAL si existe
            String facePhotoPath = person.getFacePhoto(); // la ruta existente (si ya estaba)
            if (facePhoto != null && !facePhoto.isEmpty()) {
                String baseDir = "C:/Users/Windows/Pictures/huellaskiosko/Cara";
                String userDir = baseDir + "/" + curp; // Carpeta por usuario
                File dir = new File(userDir);
                if (!dir.exists()) dir.mkdirs();

                // Elimina la foto anterior si existe
                if (facePhotoPath != null && !facePhotoPath.isBlank()) {
                    File prevFile = new File(facePhotoPath);
                    if (prevFile.exists()) {
                        boolean deleted = prevFile.delete();
                        log.info("Foto anterior eliminada: {} => {}", facePhotoPath, deleted ? "ok" : "no se pudo");
                    }
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

            // Prepara la respuesta
            FingerprintVerificationResponseDTO response = new FingerprintVerificationResponseDTO();
            response.setIdPerson(person.getIdPerson());
            response.setCurp(person.getCurp());
            response.setNombres(person.getNombres());
            response.setPrimerApellido(person.getPrimerApellido());
            response.setSegundoApellido(person.getSegundoApellido());
            response.setFechaNacimiento(person.getFechaNacimiento());
            response.setSexo(person.getSexo());
            response.setNacionalidad(person.getNacionalidad());
            response.setDireccion(person.getDireccion());
            response.setEstado(person.getEstado());
            response.setMunicipio(person.getMunicipio());
            response.setFacePhoto(facePhotoPath); // Solo la ruta

            log.info("Enviando respuesta: {}", response);

            return ResponseEntity.ok(response);

        } catch (Exception ex) {
            log.error("Error comparando huellas dactilares: ", ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}
