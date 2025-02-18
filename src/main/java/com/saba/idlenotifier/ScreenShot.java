package com.saba.idlenotifier;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class ScreenShot {
    public byte[] createScreenShot() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            Robot robot = new Robot();
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, "png", baos);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return baos.toByteArray();
    }
}
