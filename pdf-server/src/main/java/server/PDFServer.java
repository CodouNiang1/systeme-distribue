package server;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.PortableServer.*;
import PDFService.IPDFService;
import servant.PDFServiceImpl;

import java.util.Properties;

public class PDFServer {

    public static void main(String[] args) {
        try {
            System.out.println("[SERVEUR] Démarrage du serveur CORBA PDF...");

            String host = System.getProperty("corba.host", "0.0.0.0");
            String port = System.getProperty("corba.port", "1050");

            Properties props = new Properties();
            props.setProperty("org.omg.CORBA.ORBClass",
                "com.sun.corba.ee.impl.orb.ORBImpl");
            props.setProperty("org.omg.CORBA.ORBSingletonClass",
                "com.sun.corba.ee.impl.orb.ORBSingleton");
            props.setProperty("org.omg.CORBA.ORBInitialHost", host);
            props.setProperty("org.omg.CORBA.ORBInitialPort", port);
            // Démarrer le NameService embarqué
            props.setProperty("com.sun.corba.ee.POA.ORBServerId", "1");
            props.setProperty("com.sun.corba.ee.POA.ORBPersistentServerPort", port);

            ORB orb = ORB.init(new String[]{
                "-ORBInitialHost", host,
                "-ORBInitialPort", port,
                "-ORBListenEndpoints", "iiop://:" + port
            }, props);

            // Démarrer le NameService embarqué
            com.sun.corba.ee.impl.naming.cosnaming.TransientNameService tns =
                new com.sun.corba.ee.impl.naming.cosnaming.TransientNameService(
                    (com.sun.corba.ee.spi.orb.ORB) orb);

            POA rootPOA = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            rootPOA.the_POAManager().activate();

            PDFServiceImpl servant = new PDFServiceImpl();
            org.omg.CORBA.Object ref =
                rootPOA.servant_to_reference(servant);
            IPDFService pdfRef =
                PDFService.IPDFServiceHelper.narrow(ref);

            NamingContextExt ns = NamingContextExtHelper.narrow(
                orb.resolve_initial_references("NameService")
            );
            NameComponent[] path = ns.to_name("PDFService");
            ns.rebind(path, pdfRef);

            System.out.println("[SERVEUR] NameService embarqué démarré sur " + host + ":" + port);
            System.out.println("[SERVEUR] Service PDF enregistré sous 'PDFService'");
            System.out.println("[SERVEUR] En attente de requêtes...");

            orb.run();

        } catch (Exception e) {
            System.err.println("[SERVEUR] Erreur fatale : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
