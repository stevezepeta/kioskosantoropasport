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
  

    /**
     * Enrolla los datos personales y los guarda en la tabla Person,
     * guardando el NOMBRE de estado y municipio (no el id).
     */
    @Transactional
    public Person enrollBiographic(EnrollPersonDTO enrollPersonDTO) {
        // Validación de que vengan los IDs
        if (enrollPersonDTO.getEstado() == null || enrollPersonDTO.getMunicipio() == null) {
            throw new IllegalArgumentException("Estado y municipio son requeridos");
        }

        // Obtener el nombre del estado
        String nombreEstado = estadoMunicipioService.getAllEstados().stream()
                .filter(e -> e.getId().equals(enrollPersonDTO.getEstado()))
                .findFirst()
                .map(e -> e.getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Estado no válido"));

        // Obtener el nombre del municipio
        String nombreMunicipio = estadoMunicipioService.getMunicipiosPorEstado(enrollPersonDTO.getEstado()).stream()
                .filter(m -> m.getId().equals(enrollPersonDTO.getMunicipio()))
                .findFirst()
                .map(m -> m.getNombre())
                .orElseThrow(() -> new IllegalArgumentException("Municipio no válido para el estado"));

        // Crear el objeto Person desde el DTO, asignando nombres, NO ids
        Person person = Person.builder()
                .curp(enrollPersonDTO.getCurp())
                .nombres(enrollPersonDTO.getNombres())
                .primerApellido(enrollPersonDTO.getPrimerApellido())
                .segundoApellido(enrollPersonDTO.getSegundoApellido())
                .fechaNacimiento(enrollPersonDTO.getFechaNacimiento())
                .sexo(enrollPersonDTO.getSexo())
                .nacionalidad(enrollPersonDTO.getNacionalidad())
                .direccion(enrollPersonDTO.getDireccion())
                .estado(nombreEstado)       // << Guarda el NOMBRE, no el id
                .municipio(nombreMunicipio) // << Guarda el NOMBRE, no el id
                .build();

        Person savedPerson = personRepository.save(person);

        log.info("Persona enrolada exitosamente con CURP={}, Nombre={} {} Estado={} Municipio={}",
                savedPerson.getCurp(), savedPerson.getNombres(), savedPerson.getPrimerApellido(),
                savedPerson.getEstado(), savedPerson.getMunicipio());

        return savedPerson;
    }

    // ==================== BIOMETRIC ENROLLMENT =======================
    // Biometric enrollment 4th
    @Transactional
public void enrollBiometric(
        EnrollBiometricDataDTO enrollBiometricDataDTO,
        Map<String, MultipartFile> filesBiometric
) throws EnrollException, ModelNotFoundException, IOException {

    // Buscar a la persona por ID
    Person person = personService.findById(Long.valueOf(enrollBiometricDataDTO.getIdPerson()));
    //User userAuth = utilService.userInSession(); 

    if (person == null) {
        throw new ModelNotFoundException(Person.class);
    }

    // Mapea el status de cada dedo
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

    // Guarda las imágenes de las huellas, solo si corresponde
    Map<String, String> filesImages = new HashMap<>();
    for (Map.Entry<String, MultipartFile> entry : filesBiometric.entrySet()) {
        String identifier = entry.getKey();
        MultipartFile file = entry.getValue();
        if (fingerPrintStatus.get(identifier).contains("A") || 
            fingerPrintStatus.get(identifier).contains("N") || 
            fingerPrintStatus.get(identifier).contains("B")) {
            filesImages.put(identifier, null);
        } else {
            if (file != null) {
                String imageUrl = imageService.saveImage(file, identifier, enrollBiometricDataDTO.getIdPerson(), ImageService.ImageType.CUSTOMER);
                filesImages.put(identifier, imageUrl);
            } else {
                throw new EnrollException("Image not found for finger: " + identifier);
            }
        }
    }
    enrollBiometricDataDTO.setFingerPrintImages(filesImages);

    Map<String, String> fingerPrintImages = enrollBiometricDataDTO.getFingerPrintImages();

    // Construye el objeto FingerPrint y guarda
    FingerPrint userFingerPrint = FingerPrint.builder()
            //.userRegister(userAuth)
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

    log.info("Enrollment fingerprints Registered Successfully of {} {}",
            person.getNombres(), person.getPrimerApellido());
}

}