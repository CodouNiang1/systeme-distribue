package gui;

import PDFService.IPDFService;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.nio.file.Files;

public class MainController {

    private final Stage stage;
    private final IPDFService service;
    private final FileChooser fileChooser = new FileChooser();

    public MainController(Stage stage) {
        this.stage = stage;
        this.service = CORBAClient.getService();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
        );
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private byte[] choisirEtLirePDF(String titre) throws Exception {
        fileChooser.setTitle(titre);
        File f = fileChooser.showOpenDialog(stage);
        if (f == null) throw new Exception("Aucun fichier sélectionné.");
        return Files.readAllBytes(f.toPath());
    }

    private void sauvegarderPDF(byte[] data, String titre) throws Exception {
        fileChooser.setTitle(titre);
        File f = fileChooser.showSaveDialog(stage);
        if (f == null) throw new Exception("Sauvegarde annulée.");
        Files.write(f.toPath(), data);
        AlertHelper.succes("Fichier sauvegardé :\n" + f.getAbsolutePath());
    }

    private void sauvegarderImage(byte[] data, String titre) throws Exception {
        FileChooser fc = new FileChooser();
        fc.setTitle(titre);
        fc.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images PNG", "*.png")
        );
        File f = fc.showSaveDialog(stage);
        if (f == null) throw new Exception("Sauvegarde annulée.");
        Files.write(f.toPath(), data);
        AlertHelper.succes("Image sauvegardée :\n" + f.getAbsolutePath());
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    public void fusionner() {
        try {
            byte[] pdf1 = choisirEtLirePDF("Sélectionner le PDF 1");
            byte[] pdf2 = choisirEtLirePDF("Sélectionner le PDF 2");
            byte[] result = service.fusionnerPDF(pdf1, pdf2);
            sauvegarderPDF(result, "Sauvegarder le PDF fusionné");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public void decouper(int debut, int fin) {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF à découper");
            byte[] result = service.decouperPDF(pdf, debut, fin);
            sauvegarderPDF(result, "Sauvegarder le PDF découpé");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public void extrairePage(int page) {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF");
            byte[] result = service.extrairePage(pdf, page);
            sauvegarderPDF(result, "Sauvegarder la page extraite");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public void supprimerPage(int page) {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF");
            byte[] result = service.supprimerPage(pdf, page);
            sauvegarderPDF(result, "Sauvegarder le PDF modifié");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public void ajouterMotDePasse(String mdp) {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF");
            byte[] result = service.ajouterMotDePasse(pdf, mdp);
            sauvegarderPDF(result, "Sauvegarder le PDF protégé");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public void convertirEnImage(int page) {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF");
            byte[] result = service.convertirEnImage(pdf, page);
            sauvegarderImage(result, "Sauvegarder l'image");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }

    public String extraireTexte() {
        try {
            byte[] pdf = choisirEtLirePDF("Sélectionner le PDF");
            return service.extraireTexte(pdf);
        } catch (Exception e) {
            AlertHelper.erreur(e.getMessage());
            return "";
        }
    }

    public void creerPDF(String contenu) {
        try {
            byte[] result = service.creerPDF(contenu);
            sauvegarderPDF(result, "Sauvegarder le PDF créé");
        } catch (Exception e) { AlertHelper.erreur(e.getMessage()); }
    }
}
