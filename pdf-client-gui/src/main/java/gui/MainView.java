package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainView {

    private final Stage stage;
    private final MainController controller;

    public MainView(Stage stage) {
        this.stage = stage;
        this.controller = new MainController(stage);
    }

    public Scene construire() {
        // ── Titre ────────────────────────────────────────────────────────────
        Label titre = new Label("Client PDF — CORBA");
        titre.getStyleClass().add("titre");

        // ── Onglets ──────────────────────────────────────────────────────────
        TabPane tabs = new TabPane();
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(
            tabFusionner(),
            tabDecouper(),
            tabExtrairePage(),
            tabSupprimerPage(),
            tabMotDePasse(),
            tabConvertirImage(),
            tabExtraireTexte(),
            tabCreerPDF()
        );

        // ── Layout principal ──────────────────────────────────────────────────
        VBox root = new VBox(12, titre, tabs);
        root.setPadding(new Insets(20));
        root.getStyleClass().add("root");

        Scene scene = new Scene(root, 700, 520);
        scene.getStylesheets().add(
            getClass().getResource("/style.css").toExternalForm()
        );
        return scene;
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    private Tab creerTab(String nom, VBox contenu) {
        contenu.setPadding(new Insets(20));
        contenu.setSpacing(12);
        ScrollPane scroll = new ScrollPane(contenu);
        scroll.setFitToWidth(true);
        return new Tab(nom, scroll);
    }

    private TextField champNombre(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(120);
        return tf;
    }

    private Button bouton(String texte) {
        Button b = new Button(texte);
        b.getStyleClass().add("btn-action");
        return b;
    }

    private Label label(String texte) {
        Label l = new Label(texte);
        l.getStyleClass().add("label-section");
        return l;
    }

    // ── Onglet 1 : Fusionner ─────────────────────────────────────────────────
    private Tab tabFusionner() {
        Button btn = bouton("📂 Sélectionner PDF 1 & PDF 2 puis fusionner");
        btn.setOnAction(e -> controller.fusionner());
        VBox box = new VBox(12,
            label("Fusionne deux fichiers PDF en un seul."),
            btn
        );
        return creerTab("Fusionner", box);
    }

    // ── Onglet 2 : Découper ──────────────────────────────────────────────────
    private Tab tabDecouper() {
        TextField debut = champNombre("Page début");
        TextField fin   = champNombre("Page fin");
        Button btn = bouton("✂️ Découper");
        btn.setOnAction(e -> {
            try {
                controller.decouper(
                    Integer.parseInt(debut.getText().trim()),
                    Integer.parseInt(fin.getText().trim())
                );
            } catch (NumberFormatException ex) {
                AlertHelper.erreur("Entrez des numéros de page valides.");
            }
        });
        HBox champs = new HBox(10, new Label("De la page :"), debut,
                                   new Label("à :"), fin);
        champs.setAlignment(Pos.CENTER_LEFT);
        return creerTab("Découper", new VBox(12,
            label("Extrait un intervalle de pages du PDF."), champs, btn));
    }

    // ── Onglet 3 : Extraire page ─────────────────────────────────────────────
    private Tab tabExtrairePage() {
        TextField page = champNombre("N° de page");
        Button btn = bouton("📄 Extraire la page");
        btn.setOnAction(e -> {
            try {
                controller.extrairePage(Integer.parseInt(page.getText().trim()));
            } catch (NumberFormatException ex) {
                AlertHelper.erreur("Entrez un numéro de page valide.");
            }
        });
        HBox champs = new HBox(10, new Label("Page :"), page);
        champs.setAlignment(Pos.CENTER_LEFT);
        return creerTab("Extraire page", new VBox(12,
            label("Extrait une seule page du PDF."), champs, btn));
    }

    // ── Onglet 4 : Supprimer page ────────────────────────────────────────────
    private Tab tabSupprimerPage() {
        TextField page = champNombre("N° de page");
        Button btn = bouton("🗑️ Supprimer la page");
        btn.setOnAction(e -> {
            try {
                controller.supprimerPage(Integer.parseInt(page.getText().trim()));
            } catch (NumberFormatException ex) {
                AlertHelper.erreur("Entrez un numéro de page valide.");
            }
        });
        HBox champs = new HBox(10, new Label("Page :"), page);
        champs.setAlignment(Pos.CENTER_LEFT);
        return creerTab("Supprimer page", new VBox(12,
            label("Supprime une page du PDF."), champs, btn));
    }

    // ── Onglet 5 : Mot de passe ──────────────────────────────────────────────
    private Tab tabMotDePasse() {
        PasswordField mdp = new PasswordField();
        mdp.setPromptText("Mot de passe");
        mdp.setMaxWidth(250);
        Button btn = bouton("🔒 Protéger le PDF");
        btn.setOnAction(e -> {
            if (mdp.getText().isBlank()) {
                AlertHelper.erreur("Entrez un mot de passe.");
                return;
            }
            controller.ajouterMotDePasse(mdp.getText());
        });
        return creerTab("Mot de passe", new VBox(12,
            label("Chiffre le PDF avec un mot de passe (AES-128)."),
            new HBox(10, new Label("Mot de passe :"), mdp), btn));
    }

    // ── Onglet 6 : PDF → Image ───────────────────────────────────────────────
    private Tab tabConvertirImage() {
        TextField page = champNombre("N° de page");
        Button btn = bouton("🖼️ Convertir en image PNG");
        btn.setOnAction(e -> {
            try {
                controller.convertirEnImage(Integer.parseInt(page.getText().trim()));
            } catch (NumberFormatException ex) {
                AlertHelper.erreur("Entrez un numéro de page valide.");
            }
        });
        HBox champs = new HBox(10, new Label("Page :"), page);
        champs.setAlignment(Pos.CENTER_LEFT);
        return creerTab("PDF → Image", new VBox(12,
            label("Convertit une page PDF en image PNG (150 DPI)."),
            champs, btn));
    }

    // ── Onglet 7 : Extraire texte ────────────────────────────────────────────
    private Tab tabExtraireTexte() {
        TextArea zone = new TextArea();
        zone.setEditable(false);
        zone.setWrapText(true);
        zone.setPrefHeight(300);
        zone.setPromptText("Le texte extrait apparaîtra ici...");
        Button btn = bouton("📋 Extraire le texte");
        btn.setOnAction(e -> {
            String texte = controller.extraireTexte();
            zone.setText(texte);
        });
        return creerTab("Extraire texte", new VBox(12,
            label("Extrait tout le texte du PDF."), btn, zone));
    }

    // ── Onglet 8 : Créer PDF ─────────────────────────────────────────────────
    private Tab tabCreerPDF() {
        TextArea contenu = new TextArea();
        contenu.setPromptText("Tapez votre contenu ici...");
        contenu.setWrapText(true);
        contenu.setPrefHeight(280);
        Button btn = bouton("📝 Créer le PDF");
        btn.setOnAction(e -> {
            if (contenu.getText().isBlank()) {
                AlertHelper.erreur("Le contenu ne peut pas être vide.");
                return;
            }
            controller.creerPDF(contenu.getText());
        });
        return creerTab("Créer PDF", new VBox(12,
            label("Crée un nouveau PDF depuis un texte."), contenu, btn));
    }
}
