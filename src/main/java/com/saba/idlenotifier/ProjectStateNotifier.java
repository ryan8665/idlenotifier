package com.saba.idlenotifier;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public class ProjectStateNotifier implements ProjectManagerListener {

    @Override
    public void projectOpened(@NotNull Project project) {
        if (!IdleListener.getInstance().iconStatus()) {
            return;
        }

        System.out.println("Project opened: " + project.getName());
        String systemName = getSystemName();
        String ipAddress = getIpAddress();
        long time = System.currentTimeMillis();
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.sendRestMessageToServer("/project", systemName, ipAddress, time, "open", project.getName());
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        if (!IdleListener.getInstance().iconStatus()) {
            return;
        }
        System.out.println("Project closed: " + project.getName());
        String systemName = getSystemName();
        String ipAddress = getIpAddress();
        long time = System.currentTimeMillis();
        ServerConnector serverConnector = new ServerConnector();
        serverConnector.sendRestMessageToServer("/project", systemName, ipAddress, time, "close", project.getName());
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
}
