package com.saba.idlenotifier;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.util.Base64;

public class MessageCtrl extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        resp.setStatus(HttpServletResponse.SC_OK);
        String message = req.getParameter("message");
        if (message == null || message.isEmpty()) {
           return;
        }
        showDialog(message);

        resp.getWriter().println("");
    }

    public static void showDialog(String message) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Notification");
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(false);
        dialog.toFront();
        dialog.requestFocus();

        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(message, JLabel.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> {
            dialog.dispose();
        });
        panel.add(okButton, BorderLayout.SOUTH);

        dialog.add(panel);

        dialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {

            }

            @Override
            public void windowLostFocus(WindowEvent e) {

            }
        });

        dialog.setVisible(true);
    }
}
