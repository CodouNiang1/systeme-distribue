package client;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Adresse du serveur (modifiable ou passée en argument)
        String host = "localhost";
        String port = "1050";

        // Surcharge possible via arguments : java -jar client.jar monserveur.com 1050
        if (args.length >= 2) {
            host = args[0];
            port = args[1];
        }

        System.out.println("╔══════════════════════════════╗");
        System.out.println("║  Connexion au serveur CORBA  ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println("Hôte : " + host + "  Port : " + port);

        try {
            ConsoleCORBA corba = new ConsoleCORBA();
            corba.connecter(host, port);

            MenuConsole menu = new MenuConsole(corba.getService());
            menu.afficher();

        } catch (Exception e) {
            System.err.println("[ERREUR] Impossible de se connecter au serveur.");
            System.err.println("Détail : " + e.getMessage());
            System.exit(1);
        }
    }
}
