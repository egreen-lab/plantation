package org.egreen.plantation;

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.egreen.plantation.pushserver.DataEndpoint;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.servlet.ServletRegistration;
import org.glassfish.grizzly.servlet.WebappContext;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.IOException;

/**
 * The sample demonstrates how to make Jersey-Spring
 * integration work on top of Grizzly 2, including static pages served from
 * a folder or from within a jar file.
 *
 * @author Alexey Stashok
 */
public class Start {

    public static void main(String[] args) throws IOException, DeploymentException {
        // Initialize Grizzly HttpServer
        HttpServer server = new HttpServer();
        NetworkListener listener = new NetworkListener("grizzly2", "localhost", 8090);
        server.addListener(listener);

        // Initialize and add Spring-aware Jersey resource
        WebappContext ctx = new WebappContext("ctx", "/api");
        final ServletRegistration reg = ctx.addServlet("spring", new SpringServlet());
        reg.addMapping("/*");
        ctx.addContextInitParameter("contextConfigLocation", "spring-context.xml");
        ctx.addListener("org.springframework.web.context.ContextLoaderListener");
        ctx.addListener("org.springframework.web.context.request.RequestContextListener");


        Server wsServer = new Server("localhost", 8091, "/", null, DataEndpoint.class);
        wsServer.start();
        ctx.deploy(server);
        try {
            server.start();
            System.out.println();
            System.out.println("Press enter to stop the server...");
            System.in.read();
        } finally {
            server.shutdownNow();
        }


    }
}
