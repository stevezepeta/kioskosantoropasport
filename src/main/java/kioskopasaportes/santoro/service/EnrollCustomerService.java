package kioskopasaportes.santoro.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kioskopasaportes.santoro.dto.EnrollBiometricDataDTO;
import kioskopasaportes.santoro.model.FingerPrint;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.model.dto.EnrollPersonDTO;
import kioskopasaportes.santoro.repository.FingerPrintRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import kioskopasaportes.santoro.rulesException.EnrollException;
import kioskopasaportes.santoro.rulesException.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollCustomerService {

    private final PersonRepository personRepository;
    private final EstadoMunicipioService estadoMunicipioService;
    private final FingerPrintRepository fingerPrintRepository;
    private final PersonService personService;
    private final ImageService imageService;

    @Transactional
public Person enrollBiographic(EnrollPersonDTO enrollPersonDTO) {
    if (enrollPersonDTO.getEstado() == null || enrollPersonDTO.getMunicipio() == null) {
        throw new IllegalArgumentException("Estado y municipio son requeridos");
    }
    String nombreEstado = estadoMunicipioService.getAllEstados().stream()
            .filter(e -> e.getId().equals(enrollPersonDTO.getEstado()))
            .findFirst()
            .map(e -> e.getNombre())
            .orElseThrow(() -> new IllegalArgumentException("Estado no válido"));
    String nombreMunicipio = estadoMunicipioService.getMunicipiosPorEstado(enrollPersonDTO.getEstado()).stream()
            .filter(m -> m.getId().equals(enrollPersonDTO.getMunicipio()))
            .findFirst()
            .map(m -> m.getNombre())
            .orElseThrow(() -> new IllegalArgumentException("Municipio no válido para el estado"));

   String lugarNacimiento = nombreMunicipio + ", " + nombreEstado; // <--- Siempre así

Person person = Person.builder()
    .curp(enrollPersonDTO.getCurp())
    .nombres(enrollPersonDTO.getNombres())
    .apellidos(enrollPersonDTO.getApellidos())
    .fechaNacimiento(enrollPersonDTO.getFechaNacimiento())
    .sexo(enrollPersonDTO.getSexo())
    .nacionalidad(enrollPersonDTO.getNacionalidad())
    .lugarNacimiento(lugarNacimiento) // <--- Ya concatenado, NUNCA lo tomes del DTO
    .estado(nombreEstado)
    .municipio(nombreMunicipio)
    .build();


    Person savedPerson = personRepository.save(person);
    log.info("Persona enrolada exitosamente con CURP={}, Nombre completo={}, Estado={}, Municipio={}",
            savedPerson.getCurp(),
            savedPerson.getNombres() + " " + savedPerson.getApellidos(),
            savedPerson.getEstado(),
            savedPerson.getMunicipio());
    return savedPerson;
}


    // Biometric enrollment
    @Transactional
    public void enrollBiometric(
            EnrollBiometricDataDTO enrollBiometricDataDTO,
            Map<String, MultipartFile> filesBiometric
    ) throws EnrollException, ModelNotFoundException, IOException {

        Person person = personService.findById(Long.valueOf(enrollBiometricDataDTO.getIdPerson()));
        if (person == null) {
            throw new ModelNotFoundException(Person.class);
        }

        Map<String, String> fingerPrintStatus = new HashMap<>();
        fingerPrintStatus.put("indexLeft", enrollBiometricDataDTO.getIndexLeftStatus().toString());
        fingerPrintStatus.put("middleLeft", enrollBiometricDataDTO.getMiddleLeftStatus().toString());
        fingerPrintStatus.put("ringLeft", enrollBiometricDataDTO.getRingLeftStatus().toString());
        fingerPrintStatus.put("littleLeft", enrollBiometricDataDTO.getLittleLeftStatus().toString());
        fingerPrintStatus.put("thumbLeft", enrollBiometricDataDTO.getThumbLeftStatus().toString());
        fingerPrintStatus.put("thumbRight", enrollBiometricDataDTO.getThumbRightStatus().toString());
        fingerPrintStatus.put("indexRight", enrollBiometricDataDTO.getIndexRightStatus().toString());
        fingerPrintStatus.put("middleRight", enrollBiometricDataDTO.getMiddleRightStatus().toString());
        fingerPrintStatus.put("ringRight", enrollBiometricDataDTO.getRingRightStatus().toString());
        fingerPrintStatus.put("littleRight", enrollBiometricDataDTO.getLittleRightStatus().toString());

        Map<String, String> filesImages = new HashMap<>();
        String[] fingerKeys = {
            "thumbLeft", "indexLeft", "middleLeft", "ringLeft", "littleLeft",
            "thumbRight", "indexRight", "middleRight", "ringRight", "littleRight"
        };

        // Validar que al menos una huella fue enviada
        boolean atLeastOneFingerPresent = false;
        for (String finger : fingerKeys) {
            MultipartFile file = filesBiometric.get(finger);
            if (file != null && !file.isEmpty()) {
                atLeastOneFingerPresent = true;
                break;
            }
        }
        if (!atLeastOneFingerPresent) {
            throw new EnrollException("Debe enviar al menos una huella digital.");
        }

        for (String finger : fingerKeys) {
            MultipartFile file = filesBiometric.get(finger);
            String status = fingerPrintStatus.get(finger);
            if (file == null || (status != null && (status.contains("A") || status.contains("N") || status.contains("B")))) {
                filesImages.put(finger, null);
            } else {
                String imageUrl = imageService.saveImage(file, finger, enrollBiometricDataDTO.getIdPerson(), ImageService.ImageType.CUSTOMER);
                filesImages.put(finger, imageUrl);
            }
        }
        enrollBiometricDataDTO.setFingerPrintImages(filesImages);

        Map<String, String> fingerPrintImages = enrollBiometricDataDTO.getFingerPrintImages();

        FingerPrint userFingerPrint = FingerPrint.builder()
                .thumbLeft(fingerPrintImages.get("thumbLeft")).thumbLeftStatus(enrollBiometricDataDTO.getThumbLeftStatus())
                .indexLeft(fingerPrintImages.get("indexLeft")).indexLeftStatus(enrollBiometricDataDTO.getIndexLeftStatus())
                .middleLeft(fingerPrintImages.get("middleLeft")).middleLeftStatus(enrollBiometricDataDTO.getMiddleLeftStatus())
                .ringLeft(fingerPrintImages.get("ringLeft")).ringLeftStatus(enrollBiometricDataDTO.getRingLeftStatus())
                .littleLeft(fingerPrintImages.get("littleLeft")).littleLeftStatus(enrollBiometricDataDTO.getLittleLeftStatus())
                .thumbRight(fingerPrintImages.get("thumbRight")).thumbRightStatus(enrollBiometricDataDTO.getThumbRightStatus())
                .indexRight(fingerPrintImages.get("indexRight")).indexRightStatus(enrollBiometricDataDTO.getIndexRightStatus())
                .middleRight(fingerPrintImages.get("middleRight")).middleRightStatus(enrollBiometricDataDTO.getMiddleRightStatus())
                .ringRight(fingerPrintImages.get("ringRight")).ringRightStatus(enrollBiometricDataDTO.getRingRightStatus())
                .littleRight(fingerPrintImages.get("littleRight")).littleRightStatus(enrollBiometricDataDTO.getLittleRightStatus())
                .person(person)
                .date(LocalDateTime.now())
                .build();

        fingerPrintRepository.save(userFingerPrint);

     log.info("Enrollment fingerprints Registered Successfully of {}",
    person.getNombres() + " " + person.getApellidos());

    }

    /**
     * Verifica las huellas recibidas contra las almacenadas para el CURP recibido.
     * Retorna true si al menos una huella coincide.
     */
    public boolean verifyBiometric(String curp, Map<String, MultipartFile> filesBiometric) throws IOException, ModelNotFoundException {
  Person person = personRepository.findByCurp(curp)
    .orElseThrow(() -> new ModelNotFoundException(Person.class, curp));

FingerPrint fingerprints = fingerPrintRepository.findByPerson(person)
    .orElseThrow(() -> new ModelNotFoundException(FingerPrint.class, person.getIdPerson()));


    String[] fingerKeys = {
        "thumbLeft", "indexLeft", "middleLeft", "ringLeft", "littleLeft",
        "thumbRight", "indexRight", "middleRight", "ringRight", "littleRight"
    };

    for (String finger : fingerKeys) {
        MultipartFile file = filesBiometric.get(finger);
        if (file != null && !file.isEmpty()) {
            String storedFingerprintPath = getFingerprintPath(fingerprints, finger);
            if (storedFingerprintPath != null) {
                byte[] storedBytes = imageService.loadImageBytes(storedFingerprintPath);
                byte[] uploadedBytes = file.getBytes();
                if (storedBytes != null && uploadedBytes != null && java.util.Arrays.equals(storedBytes, uploadedBytes)) {
                    return true; // Coincidencia encontrada
                }
            }
        }
    }
    return false;
}


    // Utilitario: obtiene la ruta del archivo guardado para cada dedo
    private String getFingerprintPath(FingerPrint fingerprints, String finger) {
        switch (finger) {
            case "thumbLeft": return fingerprints.getThumbLeft();
            case "indexLeft": return fingerprints.getIndexLeft();
            case "middleLeft": return fingerprints.getMiddleLeft();
            case "ringLeft": return fingerprints.getRingLeft();
            case "littleLeft": return fingerprints.getLittleLeft();
            case "thumbRight": return fingerprints.getThumbRight();
            case "indexRight": return fingerprints.getIndexRight();
            case "middleRight": return fingerprints.getMiddleRight();
            case "ringRight": return fingerprints.getRingRight();
            case "littleRight": return fingerprints.getLittleRight();
            default: return null;
        }
    }
}
