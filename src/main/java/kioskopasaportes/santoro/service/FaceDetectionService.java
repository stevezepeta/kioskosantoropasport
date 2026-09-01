package kioskopasaportes.santoro.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.springframework.stereotype.Service;

@Service
public class FaceDetectionService {

    private final CascadeClassifier faceDetector;

    public FaceDetectionService() {
        this.faceDetector = cargarCascadeClassifier();
    }

    // Método que compara dos fotos de rostro (retorna true si son similares)
    public boolean compareFaces(byte[] uploaded, byte[] saved) throws IOException {
        Mat img1 = opencv_imgcodecs.imdecode(new Mat(uploaded), opencv_imgcodecs.IMREAD_COLOR);
        Mat img2 = opencv_imgcodecs.imdecode(new Mat(saved), opencv_imgcodecs.IMREAD_COLOR);

        Mat face1 = detectAndCropFace(img1);
        Mat face2 = detectAndCropFace(img2);

        if (face1 == null || face2 == null) {
            return false; // No se detectó rostro en alguna de las fotos
        }

        opencv_imgproc.resize(face1, face1, new Size(120, 120));
        opencv_imgproc.resize(face2, face2, new Size(120, 120));

        // Compara por diferencia absoluta de píxeles (NO biometría real, solo ejemplo simple)
        Mat diff = new Mat();
        opencv_core.absdiff(face1, face2, diff);
        Scalar sumDiff = opencv_core.sumElems(diff);
        double totalDiff = sumDiff.get(0) + sumDiff.get(1) + sumDiff.get(2);

        // Umbral de diferencia: ajusta según tus pruebas reales
        return totalDiff < 800000; // <--- Ajusta este valor según tus pruebas
    }

    // Detecta y recorta el rostro de una imagen (devuelve Mat del rostro o null)
    private Mat detectAndCropFace(Mat image) {
        RectVector faces = new RectVector();
        faceDetector.detectMultiScale(image, faces);

        if (faces.size() == 0) {
            return null; // No hay rostro
        }
        // Usa la primera cara detectada
        org.bytedeco.opencv.opencv_core.Rect faceRect = faces.get(0);
        return new Mat(image, faceRect).clone();
    }

    // === MÉTODO CLAVE PARA CARGAR EL CASCADE DESDE RESOURCES O JAR ===
    private CascadeClassifier cargarCascadeClassifier() {
        try {
            InputStream is = getClass().getResourceAsStream("/haarcascades/haarcascade_frontalface_default.xml");
            if (is == null) {
                throw new RuntimeException("No se encontró el cascade en resources");
            }
            File tempFile = File.createTempFile("haarcascade_frontalface_default", ".xml");
            tempFile.deleteOnExit();
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
            }
            return new CascadeClassifier(tempFile.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error cargando CascadeClassifier", ex);
        }
    }
}
