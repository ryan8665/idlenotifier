package com.saba.idlenotifier;


import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServer {
    private static Server server;

    public static void startServer() {
        if (server != null && server.isRunning()) {
            return;
        }

        server = new Server(42425);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MessageCtrl()), "/api/message");
        context.addServlet(new ServletHolder(new ScreenShotCtrl()), "/api/screenshot");

        try {
            server.start();
            System.out.println("Jetty REST Server started on port 42425");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}