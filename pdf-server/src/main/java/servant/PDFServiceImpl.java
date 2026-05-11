package servant;

import PDFService.*;
import utils.PDFBoxHelper;

public class PDFServiceImpl extends IPDFServicePOA {

    private final PDFBoxHelper helper = new PDFBoxHelper();

    @Override
    public byte[] fusionnerPDF(byte[] pdf1, byte[] pdf2) throws PDFException {
        try {
            return helper.fusionner(pdf1, pdf2);
        } catch (Exception e) {
            throw new PDFException("Erreur fusion : " + e.getMessage());
        }
    }

    @Override
    public byte[] decouperPDF(byte[] pdf, int debut, int fin) throws PDFException {
        try {
            return helper.decouper(pdf, debut, fin);
        } catch (Exception e) {
            throw new PDFException("Erreur découpage : " + e.getMessage());
        }
    }

    @Override
    public byte[] extrairePage(byte[] pdf, int numPage) throws PDFException {
        try {
            return helper.extrairePage(pdf, numPage);
        } catch (Exception e) {
            throw new PDFException("Erreur extraction page : " + e.getMessage());
        }
    }

    @Override
    public byte[] supprimerPage(byte[] pdf, int numPage) throws PDFException {
        try {
            return helper.supprimerPage(pdf, numPage);
        } catch (Exception e) {
            throw new PDFException("Erreur suppression page : " + e.getMessage());
        }
    }

    @Override
    public byte[] ajouterMotDePasse(byte[] pdf, String motDePasse) throws PDFException {
        try {
            return helper.ajouterMotDePasse(pdf, motDePasse);
        } catch (Exception e) {
            throw new PDFException("Erreur mot de passe : " + e.getMessage());
        }
    }

    @Override
    public byte[] convertirEnImage(byte[] pdf, int numPage) throws PDFException {
        try {
            return helper.convertirEnImage(pdf, numPage);
        } catch (Exception e) {
            throw new PDFException("Erreur conversion image : " + e.getMessage());
        }
    }

    @Override
    public String extraireTexte(byte[] pdf) throws PDFException {
        try {
            return helper.extraireTexte(pdf);
        } catch (Exception e) {
            throw new PDFException("Erreur extraction texte : " + e.getMessage());
        }
    }

    @Override
    public byte[] creerPDF(String contenu) throws PDFException {
        try {
            return helper.creerPDF(contenu);
        } catch (Exception e) {
            throw new PDFException("Erreur création PDF : " + e.getMessage());
        }
    }
}
