package client;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import PDFService.IPDFService;
import PDFService.IPDFServiceHelper;
import java.util.Properties;

public class ConsoleCORBA {

    private IPDFService service;

    public void connecter(String host, String port) throws Exception {
        Properties props = new Properties();
        props.setProperty("org.omg.CORBA.ORBInitialHost", host);
        props.setProperty("org.omg.CORBA.ORBInitialPort", port);

        ORB orb = ORB.init(new String[]{}, props);

        NamingContextExt ns = NamingContextExtHelper.narrow(
            orb.resolve_initial_references("NameService")
        );

        service = IPDFServiceHelper.narrow(
            ns.resolve_str("PDFService")
        );

        System.out.println("[CLIENT] Connecté au serveur CORBA (" + host + ":" + port + ")");
    }

    public IPDFService getService() {
        return service;
    }
}
