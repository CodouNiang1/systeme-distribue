package api;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.springframework.stereotype.Service;
import PDFService.IPDFService;
import PDFService.IPDFServiceHelper;
import servant.PDFServiceImpl;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
public class CORBAService {

    private IPDFService service;

    @PostConstruct
    public void init() throws Exception {
        Properties props = new Properties();
        props.setProperty("org.omg.CORBA.ORBClass",
            "com.sun.corba.ee.impl.orb.ORBImpl");
        props.setProperty("org.omg.CORBA.ORBSingletonClass",
            "com.sun.corba.ee.impl.orb.ORBSingleton");
        props.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        props.setProperty("org.omg.CORBA.ORBInitialPort", "1050");

        ORB orb = ORB.init(new String[]{}, props);

        // Démarrer le NameService embarqué
        com.sun.corba.ee.impl.naming.cosnaming.TransientNameService tns =
            new com.sun.corba.ee.impl.naming.cosnaming.TransientNameService(
                (com.sun.corba.ee.spi.orb.ORB) orb);

        // Démarrer le POA
        POA rootPOA = POAHelper.narrow(
            orb.resolve_initial_references("RootPOA"));
        rootPOA.the_POAManager().activate();

        // Enregistrer le servant PDFService
        PDFServiceImpl servant = new PDFServiceImpl();
        org.omg.CORBA.Object ref = rootPOA.servant_to_reference(servant);
        IPDFService pdfRef = IPDFServiceHelper.narrow(ref);

        NamingContextExt ns = NamingContextExtHelper.narrow(
            orb.resolve_initial_references("NameService"));
        ns.rebind(ns.to_name("PDFService"), pdfRef);

        service = pdfRef;

        // Lancer l'ORB dans un thread séparé
        new Thread(() -> orb.run()).start();

        System.out.println("[SPRING] Serveur CORBA embarqué démarré !");
    }

    public IPDFService getService() {
        return service;
    }
}
