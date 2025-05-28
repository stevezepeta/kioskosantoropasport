package kioskopasaportes.santoro.service;

import kioskopasaportes.santoro.model.Image;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.repository.ImageRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final PersonRepository personRepository;

    // directorio de subida de archivos de usuarios
    @Value("${file.uploadusr-dir}")
    private String uploadUsersDir;

    // directorio de subida de archivos de clientes
    @Value("${file.uploadcus-dir}")
    private String uploadCustomersDir;

    public String saveImage(MultipartFile file, String key, String idPerson, ImageType imageType) throws IOException {

        if (file.isEmpty()) {
            throw new IOException("No se ha seleccionado ningún archivo");
        }
        if (file.getOriginalFilename() == null) {
            throw new IOException("El archivo no tiene nombre");
        }

        // buscar la persona por su ID
        Optional<Person> person = personRepository.findById(Long.valueOf(idPerson));
        if (person.isEmpty()) {
            throw new IOException("No se ha encontrado la persona con ID: " + idPerson);
        }

        String nombres = person.get().getNombres(); // Adaptado a tu modelo (nombres)
        String primerApellido = person.get().getPrimerApellido() != null ? person.get().getPrimerApellido() : "";
        String segundoApellido = person.get().getSegundoApellido() != null ? person.get().getSegundoApellido() : "";

        String formattedSurname = "";
        if (!primerApellido.isEmpty()) {
            formattedSurname = Character.toUpperCase(primerApellido.charAt(0)) + primerApellido.substring(1);
        }

        String personFinder = ((nombres != null ? nombres : "") + formattedSurname + "ID" + idPerson)
                .replaceAll("\\s+", "");

        // elegir el directorio de subida de archivos según el tipo de imagen
        String uploadDir = switch (imageType) {
            case USER -> uploadUsersDir;
            case CUSTOMER -> uploadCustomersDir;
        };

        // Obtener la fecha actual
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Crear el directorio con la fecha si no existe
        Path datePath = Paths.get(uploadDir, currentDate);
        if (!Files.exists(datePath)) {
            Files.createDirectories(datePath);
        }

        // Crear el subdirectorio específico para la persona dentro del directorio con la fecha
        Path personPath = datePath.resolve(personFinder);
        if (!Files.exists(personPath)) {
            Files.createDirectories(personPath);
        }

        // Guardar la imagen con el identificador
        String fileName = StringUtils.cleanPath(key + "_" + file.getOriginalFilename());
        Path filePath = personPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Guardar la ruta en la base de datos
        Image image = new Image();
        image.setFilePath(filePath.toString());
        imageRepository.save(image);

        return filePath.toString();
    }

    public String convertImageToBase64(String imagePath) throws IOException {
        if (imagePath != null) {
            Path path = Paths.get(imagePath);
            byte[] imageBytes = Files.readAllBytes(path);

            // Convertir los bytes a una cadena en base64
            return Base64.getEncoder().encodeToString(imageBytes);
        } else {
            return null;
        }
    }

    public enum ImageType {
        USER, CUSTOMER
    }
}
