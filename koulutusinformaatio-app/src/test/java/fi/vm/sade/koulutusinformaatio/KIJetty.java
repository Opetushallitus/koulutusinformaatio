package fi.vm.sade.koulutusinformaatio;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KIJetty {

    static final String KI_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/koulutusinformaatio-app";

    public static void main(String[] args) throws Exception {
        System.setProperty("PRERENDER_SERVICE_URL", "http://localhost:3000/");

        Server server = new Server(8080);
        ExecutorService executorService =
            Executors.newFixedThreadPool(10, new CustomizableThreadFactory("ki-test-jetty-"));
        server.setThreadPool(new ExecutorThreadPool(executorService));

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
