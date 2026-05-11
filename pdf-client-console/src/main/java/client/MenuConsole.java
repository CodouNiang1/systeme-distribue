package client;

import PDFService.IPDFService;
import PDFService.PDFException;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class MenuConsole {

    private final IPDFService service;
    private final Scanner scanner;

    public MenuConsole(IPDFService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void afficher() {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║     CLIENT PDF - CORBA       ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. Fusionner deux PDFs       ║");
            System.out.println("║ 2. Découper un PDF           ║");
            System.out.println("║ 3. Extraire une page         ║");
            System.out.println("║ 4. Supprimer une page        ║");
            System.out.println("║ 5. Ajouter un mot de passe   ║");
            System.out.println("║ 6. Convertir page en image   ║");
            System.out.println("║ 7. Extraire le texte         ║");
            System.out.println("║ 8. Créer un PDF              ║");
            System.out.println("║ 0. Quitter                   ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Choix : ");

            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1": fusionner();         break;
                    case "2": decouper();          break;
                    case "3": extrairePage();      break;
                    case "4": supprimerPage();     break;
                    case "5": ajouterMotDePasse(); break;
                    case "6": convertirEnImage();  break;
                    case "7": extraireTexte();     break;
                    case "8": creerPDF();          break;
                    case "0": continuer = false;   break;
                    default:  System.out.println("[!] Choix invalide.");
                }
            } catch (PDFException e) {
                System.out.println("[ERREUR SERVEUR] " + e.message);
            } catch (Exception e) {
                System.out.println("[ERREUR] " + e.getMessage());
            }
        }
        System.out.println("Au revoir.");
    }

    // ── Helpers I/O ──────────────────────────────────────────────────────────

    private byte[] lireFichier(String prompt) throws IOException {
        System.out.print(prompt);
        String chemin = scanner.nextLine().trim();
        return Files.readAllBytes(Paths.get(chemin));
    }

    private void sauvegarderFichier(byte[] data, String prompt) throws IOException {
        System.out.print(prompt);
        String chemin = scanner.nextLine().trim();
        Files.write(Paths.get(chemin), data);
        System.out.println("[OK] Fichier sauvegardé : " + chemin);
    }

    private int lireEntier(String prompt) {
        System.out.print(prompt);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    private void fusionner() throws Exception {
        byte[] pdf1 = lireFichier("Chemin PDF 1 : ");
        byte[] pdf2 = lireFichier("Chemin PDF 2 : ");
        byte[] result = service.fusionnerPDF(pdf1, pdf2);
        sauvegarderFichier(result, "Sauvegarder sous : ");
    }

    private void decouper() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        int debut = lireEntier("Page début : ");
        int fin   = lireEntier("Page fin   : ");
        byte[] result = service.decouperPDF(pdf, debut, fin);
        sauvegarderFichier(result, "Sauvegarder sous : ");
    }

    private void extrairePage() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        int page = lireEntier("Numéro de page : ");
        byte[] result = service.extrairePage(pdf, page);
        sauvegarderFichier(result, "Sauvegarder sous : ");
    }

    private void supprimerPage() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        int page = lireEntier("Page à supprimer : ");
        byte[] result = service.supprimerPage(pdf, page);
        sauvegarderFichier(result, "Sauvegarder sous : ");
    }

    private void ajouterMotDePasse() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        System.out.print("Mot de passe : ");
        String mdp = scanner.nextLine().trim();
        byte[] result = service.ajouterMotDePasse(pdf, mdp);
        sauvegarderFichier(result, "Sauvegarder sous : ");
    }

    private void convertirEnImage() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        int page = lireEntier("Numéro de page : ");
        byte[] result = service.convertirEnImage(pdf, page);
        sauvegarderFichier(result, "Sauvegarder image sous (.png) : ");
    }

    private void extraireTexte() throws Exception {
        byte[] pdf = lireFichier("Chemin PDF : ");
        String texte = service.extraireTexte(pdf);
        System.out.println("\n── Texte extrait ──────────────────");
        System.out.println(texte);
        System.out.println("───────────────────────────────────");
        System.out.print("Sauvegarder dans un fichier .txt ? (o/n) : ");
        if (scanner.nextLine().trim().equalsIgnoreCase("o")) {
            sauvegarderFichier(texte.getBytes(), "Sauvegarder sous : ");
        }
    }

    private void creerPDF() throws Exception {
        System.out.println("Entrez le contenu (tapez FIN sur une ligne seule pour terminer) :");
        StringBuilder sb = new StringBuilder();
        String ligne;
        while (!(ligne = scanner.nextLine()).equals("FIN")) {
            sb.append(ligne).append("\n");
        }
        // Demander le chemin APRES que le contenu soit terminé
        System.out.print("Sauvegarder sous (ex: /mnt/c/Users/Codou/rapport.pdf) : ");
        String chemin = scanner.nextLine().trim();
        byte[] result = service.creerPDF(sb.toString());
        Files.write(Paths.get(chemin), result);
        System.out.println("[OK] PDF créé : " + chemin);
    }
}
