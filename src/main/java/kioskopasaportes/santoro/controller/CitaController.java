package kioskopasaportes.santoro.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import kioskopasaportes.santoro.dto.CitaRequestDTO;
import kioskopasaportes.santoro.dto.OficinaDTO;
import kioskopasaportes.santoro.model.Cita;
import kioskopasaportes.santoro.model.Person;
import kioskopasaportes.santoro.repository.CitaRepository;
import kioskopasaportes.santoro.repository.PersonRepository;
import kioskopasaportes.santoro.service.OficinaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cita")
@RequiredArgsConstructor
@Slf4j
public class CitaController {

    private final CitaRepository citaRepository;
    private final PersonRepository personRepository;
    private final OficinaService oficinaService;

    @PostMapping("/{personId}")
    public ResponseEntity<?> crearCita(
        @PathVariable Long personId,
        @RequestBody CitaRequestDTO request
    ) {
        Person person = personRepository.findById(personId).orElse(null);
        if (person == null) {
            return ResponseEntity.badRequest().body("Persona no encontrada");
        }

        // Buscar oficina en el catálogo JSON
        OficinaDTO oficina = oficinaService.getOficinasPorEstado(request.getEstadoId())
                                           .stream()
                                           .filter(o -> o.getId().equals(request.getOficinaId()))
                                           .findFirst()
                                           .orElse(null);

        if (oficina == null) {
            return ResponseEntity.badRequest().body("Oficina no encontrada");
        }

        // Generar folio único
        String folio = "CITA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Generar QR con el folio
        byte[] qrBytes = generateQRCodeImage(folio);

        Cita cita = new Cita();
        cita.setCiudadanoIdExterno(person.getIdPerson());
        cita.setPersonId(person.getIdPerson());
        cita.setOficinaId(oficina.getId());
        cita.setOficinaNombre(oficina.getNombre());
        cita.setFechaCita(LocalDate.parse(request.getFecha()));
        cita.setHoraCita(LocalTime.parse(request.getHora()));
        cita.setFolio(folio); // <-- asegúrate de tener este campo en la entidad y la tabla
        cita.setCodigoQr(qrBytes);

        citaRepository.save(cita);

        // Respuesta tipo ticket
        LinkedHashMap<String, Object> ticket = new LinkedHashMap<>();
        ticket.put("folio", folio);
        ticket.put("fecha_hora_generacion", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(OffsetDateTime.now()));
        ticket.put("oficina", oficina.getNombre());
        ticket.put("direccion", oficina.getDireccion());
        ticket.put("fecha_cita", cita.getFechaCita());
        ticket.put("hora_cita", cita.getHoraCita());
        // El QR en Base64 para mostrar como imagen en el frontend (src="data:image/png;base64,...")
        ticket.put("codigo_qr", qrBytes != null ? Base64.getEncoder().encodeToString(qrBytes) : null);

        return ResponseEntity.ok(ticket);
    }

    // === Utilidad para generar QR ===
    public static byte[] generateQRCodeImage(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
