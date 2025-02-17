package com.saba.idlenotifier;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import javax.swing.*;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import java.awt.*;

public class IdleNotifierWidgetProvider implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Are you working remotely?");
        panel.add(label, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton yesButton = new JButton("Yes");
        yesButton.addActionListener(e -> {
            updateIcon(toolWindow, "/icons/start.png");
            showStatusDialog("Status changed to: Working remotely");
            IdleListener.getInstance().startWorking();
        });
        buttonPanel.add(yesButton);

        JButton noButton = new JButton("No");
        noButton.addActionListener(e -> {
            updateIcon(toolWindow, "/icons/stop.png");
            showStatusDialog("Status changed to: Not working remotely");
            IdleListener.getInstance().stopWorking();
        });
        buttonPanel.add(noButton);

        gbc.gridy = 1;
        panel.add(buttonPanel, gbc);

        toolWindow.setIcon(getIcon("/icons/stop.png"));

        SimpleToolWindowPanel simpleToolWindowPanel = new SimpleToolWindowPanel(true);
        simpleToolWindowPanel.setContent(panel);

        toolWindow.getComponent().add(simpleToolWindowPanel);

        if (IdleListener.getInstance().iconStatus()) {
            updateIcon(toolWindow, "/icons/start.png");
        } else {
            updateIcon(toolWindow, "/icons/stop.png");
        }
    }

    private Icon getIcon(String iconPath) {
        try {
            return IconLoader.getIcon(iconPath, getClass());
        } catch (Exception e) {
            System.err.println("Icon not found: " + iconPath);
            return null;
        }
    }

    private void updateIcon(ToolWindow toolWindow, String iconPath) {
        Icon icon = getIcon(iconPath);
        if (icon != null) {
            toolWindow.setIcon(icon);
        } else {
            System.err.println("Icon not found: " + iconPath);
        }
    }

    private void showStatusDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Status Changed", JOptionPane.INFORMATION_MESSAGE);
    }
}
