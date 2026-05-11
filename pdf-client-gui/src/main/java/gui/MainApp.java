package gui;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.Optional;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        // Dialogue de connexion au démarrage
        TextField host = new TextField("localhost");
        TextField port = new TextField("1050");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.add(new Label("Hôte CORBA :"), 0, 0);
        grid.add(host, 1, 0);
        grid.add(new Label("Port :"), 0, 1);
        grid.add(port, 1, 1);

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Connexion CORBA");
        dialog.setHeaderText("Paramètres de connexion au serveur");
        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            System.exit(0);
        }

        try {
            CORBAClient.connecter(host.getText().trim(), port.getText().trim());
        } catch (Exception e) {
            Alert err = new Alert(Alert.AlertType.ERROR);
            err.setTitle("Erreur de connexion");
            err.setContentText("Impossible de se connecter au serveur CORBA.\n" + e.getMessage());
            err.showAndWait();
            System.exit(1);
        }

        // Construire et afficher la fenêtre principale
        MainView view = new MainView(stage);
        stage.setTitle("Client PDF — CORBA");
        stage.setScene(view.construire());
        stage.setMinWidth(700);
        stage.setMinHeight(520);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
