package fi.vm.sade.koulutusinformaatio;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class KIJetty {

    static final String KI_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/koulutusinformaatio-app";

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        WebAppContext context = new WebAppContext();
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase(KI_MODULE_ROOT + "/src/main/webapp/");
//        context.setContextPath("/koulutusinformaatio-app");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.join();
    }


}
