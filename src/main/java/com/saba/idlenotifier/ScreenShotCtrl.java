package com.saba.idlenotifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

public class ScreenShotCtrl extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        ScreenShot screenShot = new ScreenShot();

        String encodedScreenshot = Base64.getEncoder().encodeToString(screenShot.createScreenShot());

        resp.getWriter().println(encodedScreenshot);
    }
}
