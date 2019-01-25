package fi.vm.sade.koulutusinformaatio;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;

public class KIJetty {
    static final String KI_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/koulutusinformaatio-app";
    private static final String PRERENDER_SERVICE_URL = "PRERENDER_SERVICE_URL";

    public static void main(String[] args) throws Exception {
        if (StringUtils.isBlank(System.getProperty(PRERENDER_SERVICE_URL))) {
            System.setProperty(PRERENDER_SERVICE_URL, "http://localhost:3000/");
        }
        System.err.printf("Using PRERENDER_SERVICE_URL '%s'%n", System.getProperty(PRERENDER_SERVICE_URL));

        Server server = new Server(new ExecutorThreadPool(10));
        ServerConnector serverConnector = new ServerConnector(server);
        serverConnector.setPort(8080);
        server.setConnectors(new Connector[] { serverConnector } );

        WebAppContext context = new WebAppContext();
        context.setDescriptor("src/main/webapp/WEB-INF/web.xml");
        context.setResourceBase(KI_MODULE_ROOT + "/src/main/webapp/");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);
        server.start();
        server.join();
    }


}
