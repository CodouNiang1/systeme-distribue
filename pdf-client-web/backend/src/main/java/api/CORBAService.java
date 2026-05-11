package api;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import PDFService.IPDFService;
import PDFService.IPDFServiceHelper;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Service
public class CORBAService {

    @Value("${corba.host:localhost}")
    private String corbHost;

    @Value("${corba.port:1050}")
    private String corbaPort;

    private IPDFService service;

    @PostConstruct
    public void init() throws Exception {
        Properties props = new Properties();
        props.setProperty("org.omg.CORBA.ORBClass",
            "com.sun.corba.ee.impl.orb.ORBImpl");
        props.setProperty("org.omg.CORBA.ORBSingletonClass",
            "com.sun.corba.ee.impl.orb.ORBSingleton");
        props.setProperty("org.omg.CORBA.ORBInitialHost", corbHost);
        props.setProperty("org.omg.CORBA.ORBInitialPort", corbaPort);

        ORB orb = ORB.init(new String[]{}, props);
        NamingContextExt ns = NamingContextExtHelper.narrow(
            orb.resolve_initial_references("NameService")
        );
        service = IPDFServiceHelper.narrow(ns.resolve_str("PDFService"));
        System.out.println("[SPRING] Connecté au serveur CORBA "
            + corbHost + ":" + corbaPort);
    }

    public IPDFService getService() {
        return service;
    }
}
