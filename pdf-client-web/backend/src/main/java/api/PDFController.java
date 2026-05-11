package api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pdf")
@CrossOrigin(origins = "*")
public class PDFController {

    private final CORBAService corba;

    public PDFController(CORBAService corba) {
        this.corba = corba;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ResponseEntity<byte[]> pdfResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(data);
    }

    private ResponseEntity<byte[]> imageResponse(byte[] data, String filename) {
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"")
            .contentType(MediaType.IMAGE_PNG)
            .body(data);
    }

    // ── Endpoints ─────────────────────────────────────────────────────────────

    @PostMapping("/fusionner")
    public ResponseEntity<byte[]> fusionner(
            @RequestParam("pdf1") MultipartFile pdf1,
            @RequestParam("pdf2") MultipartFile pdf2) throws Exception {
        byte[] result = corba.getService()
            .fusionnerPDF(pdf1.getBytes(), pdf2.getBytes());
        return pdfResponse(result, "fusion.pdf");
    }

    @PostMapping("/decouper")
    public ResponseEntity<byte[]> decouper(
            @RequestParam("pdf") MultipartFile pdf,
            @RequestParam("debut") int debut,
            @RequestParam("fin") int fin) throws Exception {
        byte[] result = corba.getService()
            .decouperPDF(pdf.getBytes(), debut, fin);
        return pdfResponse(result, "decoupage.pdf");
    }

    @PostMapping("/extraire-page")
    public ResponseEntity<byte[]> extrairePage(
            @RequestParam("pdf") MultipartFile pdf,
            @RequestParam("page") int page) throws Exception {
        byte[] result = corba.getService()
            .extrairePage(pdf.getBytes(), page);
        return pdfResponse(result, "page_" + page + ".pdf");
    }

    @PostMapping("/supprimer-page")
    public ResponseEntity<byte[]> supprimerPage(
            @RequestParam("pdf") MultipartFile pdf,
            @RequestParam("page") int page) throws Exception {
        byte[] result = corba.getService()
            .supprimerPage(pdf.getBytes(), page);
        return pdfResponse(result, "modifie.pdf");
    }

    @PostMapping("/mot-de-passe")
    public ResponseEntity<byte[]> ajouterMotDePasse(
            @RequestParam("pdf") MultipartFile pdf,
            @RequestParam("mdp") String mdp) throws Exception {
        byte[] result = corba.getService()
            .ajouterMotDePasse(pdf.getBytes(), mdp);
        return pdfResponse(result, "protege.pdf");
    }

    @PostMapping("/convertir-image")
    public ResponseEntity<byte[]> convertirImage(
            @RequestParam("pdf") MultipartFile pdf,
            @RequestParam("page") int page) throws Exception {
        byte[] result = corba.getService()
            .convertirEnImage(pdf.getBytes(), page);
        return imageResponse(result, "page_" + page + ".png");
    }

    @PostMapping("/extraire-texte")
    public ResponseEntity<String> extraireTexte(
            @RequestParam("pdf") MultipartFile pdf) throws Exception {
        String texte = corba.getService().extraireTexte(pdf.getBytes());
        return ResponseEntity.ok(texte);
    }

    @PostMapping("/creer")
    public ResponseEntity<byte[]> creerPDF(
            @RequestParam("contenu") String contenu) throws Exception {
        byte[] result = corba.getService().creerPDF(contenu);
        return pdfResponse(result, "nouveau.pdf");
    }
}
