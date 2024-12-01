package com.example.idlenotifier;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ApplicationListener;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.util.Random;

public class IdleListener implements ApplicationActivationListener, ApplicationListener {
    private static final int USER_RESPONSE_TIMEOUT = 30;
    private Timer idleTimer;
    private static boolean notificationDisplayed = false;
    private Long idleTimeStart;
    private final Random random = new Random();
    private int randomIdleTimeoutMinutes;
    private static boolean isWorking = true;

    private static IdleListener instance;

    private IdleListener() {
        setupIdleDetection();
        ApplicationManager.getApplication().addApplicationListener(this);

        ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
            @Override
            public void appStarted() {
                if(!isWorking){
                    return;
                }
                String systemName = getSystemName();
                String ipAddress = getIpAddress();
                long time = System.currentTimeMillis();
                ServerConnector serverConnector = new ServerConnector();
                serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "Activate", "");
                System.out.println("Activate");
            }

            @Override
            public void appWillBeClosed(boolean isRestart) {
                if(!isWorking){
                    return;
                }
                String systemName = getSystemName();
                String ipAddress = getIpAddress();
                long time = System.currentTimeMillis();
                ServerConnector serverConnector = new ServerConnector();
                serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "DeActivate", "");
                System.out.println("DeActivate");
            }
        });
    }

    public static IdleListener getInstance() {
        if (instance == null) {
            instance = new IdleListener();
        }
        return instance;
    }

    private void setupIdleDetection() {
        resetRandomIdleTimeout();
        idleTimer = new Timer(randomIdleTimeoutMinutes * 60 * 1000, e -> showIdleNotification());
        idleTimer.setRepeats(false);

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(java.awt.AWTEvent event) {
                if (event instanceof KeyEvent || event instanceof MouseEvent) {
                    resetIdleTimer();
                }
            }
        }, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);

        idleTimer.start();
    }

    private void resetRandomIdleTimeout() {
        randomIdleTimeoutMinutes = getRandomIdleTimeout(7, 13);
    }

    private int getRandomIdleTimeout(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private void resetIdleTimer() {
        System.out.println("Resetting idle timer");
        if (idleTimer.isRunning()) {
            idleTimer.stop();
        }
        resetRandomIdleTimeout();
        idleTimer.setInitialDelay(randomIdleTimeoutMinutes * 60 * 1000);
        idleTimer.restart();
    }

    private void showIdleNotification() {
        if(!isWorking){
            if (idleTimer.isRunning()) {
                idleTimer.stop();
            }
            return;
        }
        if (!notificationDisplayed) {
            if (idleTimer.isRunning()) {
                idleTimer.stop();
            }

            Timer userResponseTimer = new Timer(USER_RESPONSE_TIMEOUT * 1000, e -> {
                idleTimeStart = System.currentTimeMillis();
                String systemName = getSystemName();
                String ipAddress = getIpAddress();
                long time = System.currentTimeMillis();
                ServerConnector serverConnector = new ServerConnector();
                serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "DeActivate", "");
            });
            userResponseTimer.setRepeats(false);

            userResponseTimer.start();

            String message = String.format("System has been idle for %d minutes!", randomIdleTimeoutMinutes);

            JDialog dialog = new JDialog();
            dialog.setTitle("Idle Notification");
            dialog.setSize(300, 150);
            dialog.setLocationRelativeTo(null);
            dialog.setAlwaysOnTop(true);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            JLabel label = new JLabel(message, JLabel.CENTER);
            panel.add(label, BorderLayout.CENTER);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                dialog.dispose();
                resetIdleTimer();
                notificationDisplayed = false;

                String systemName = getSystemName();
                String ipAddress = getIpAddress();
                long time = System.currentTimeMillis();
                ServerConnector serverConnector = new ServerConnector();
                serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "Activate", "");
                userResponseTimer.stop();
                resetRandomIdleTimeout();
            });
            panel.add(okButton, BorderLayout.SOUTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);

            notificationDisplayed = true;
        }
    }

    private String getSystemName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private String getIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public void startWorking() {
        isWorking = true;
        resetIdleTimer();
        ServerConnector serverConnector = new ServerConnector();
        String systemName = getSystemName();
        String ipAddress = getIpAddress();
        long time = System.currentTimeMillis();
        serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "Activate", "");
    }

    public void stopWorking() {
        isWorking = false;
        if (idleTimer.isRunning()) {
            idleTimer.stop();
        }
        ServerConnector serverConnector = new ServerConnector();
        String systemName = getSystemName();
        String ipAddress = getIpAddress();
        long time = System.currentTimeMillis();
        serverConnector.sendRestMessageToServer("/idle", systemName, ipAddress, time, "DeActivate", "");
    }

    public boolean iconStatus(){
       return isWorking;
    }

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        if (isWorking) {
            resetIdleTimer();
            notificationDisplayed = false;
        }
    }

    @Override
    public void applicationDeactivated(@NotNull IdeFrame ideFrame) {

    }
}
