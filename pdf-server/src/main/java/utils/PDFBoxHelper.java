package utils;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.encryption.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class PDFBoxHelper {

    // ── Fusion de deux PDFs ──────────────────────────────────────────────────
    public byte[] fusionner(byte[] pdf1, byte[] pdf2) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        merger.addSource(new ByteArrayInputStream(pdf1));
        merger.addSource(new ByteArrayInputStream(pdf2));
        merger.setDestinationStream(out);
        merger.mergeDocuments(null);
        return out.toByteArray();
    }

    // ── Découpage (pages debut à fin, index 1-based) ─────────────────────────
    public byte[] decouper(byte[] pdf, int debut, int fin) throws IOException {
        try (PDDocument doc = PDDocument.load(pdf)) {
            Splitter splitter = new Splitter();
            splitter.setStartPage(debut);
            splitter.setEndPage(fin);
            splitter.setSplitAtPage(fin - debut + 1);
            List<PDDocument> pages = splitter.split(doc);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            pages.get(0).save(out);
            pages.forEach(p -> { try { p.close(); } catch (IOException ignored) {} });
            return out.toByteArray();
        }
    }

    // ── Extraction d'une page ────────────────────────────────────────────────
    public byte[] extrairePage(byte[] pdf, int numPage) throws IOException {
        return decouper(pdf, numPage, numPage);
    }

    // ── Suppression d'une page (index 1-based) ───────────────────────────────
    public byte[] supprimerPage(byte[] pdf, int numPage) throws IOException {
        try (PDDocument doc = PDDocument.load(pdf)) {
            doc.removePage(numPage - 1); // PDFBox est 0-based
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    // ── Ajout d'un mot de passe (chiffrement AES 128) ───────────────────────
    public byte[] ajouterMotDePasse(byte[] pdf, String motDePasse) throws IOException {
        try (PDDocument doc = PDDocument.load(pdf)) {
            AccessPermission permissions = new AccessPermission();
            StandardProtectionPolicy policy =
                new StandardProtectionPolicy(motDePasse, motDePasse, permissions);
            policy.setEncryptionKeyLength(128);
            doc.protect(policy);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }

    // ── Conversion PDF → Image PNG (DPI 150) ────────────────────────────────
    public byte[] convertirEnImage(byte[] pdf, int numPage) throws IOException {
        try (PDDocument doc = PDDocument.load(pdf)) {
            PDFRenderer renderer = new PDFRenderer(doc);
            BufferedImage image = renderer.renderImageWithDPI(numPage - 1, 150);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", out);
            return out.toByteArray();
        }
    }

    // ── Extraction du texte complet ──────────────────────────────────────────
    public String extraireTexte(byte[] pdf) throws IOException {
        try (PDDocument doc = PDDocument.load(pdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    // ── Création d'un PDF depuis un texte ────────────────────────────────────
    public byte[] creerPDF(String contenu) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);
            try (PDPageContentStream stream =
                     new PDPageContentStream(doc, page)) {
                stream.beginText();
                stream.setFont(PDType1Font.HELVETICA, 12);
                stream.setLeading(16f);
                stream.newLineAtOffset(50, 750);
                // Découper le texte en lignes
                for (String ligne : contenu.split("\n")) {
                    // Tronquer si la ligne est trop longue
                    if (ligne.length() > 90) ligne = ligne.substring(0, 90);
                    stream.showText(ligne);
                    stream.newLine();
                }
                stream.endText();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out);
            return out.toByteArray();
        }
    }
}
