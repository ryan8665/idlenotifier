package com.saba.idlenotifier;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ServerConnector {

    private static final String SERVER_URL = "https://tw.sabapardazesh.net/api";

    public void sendRestMessageToServer(String endpoint, String systemName, String ipAddress, long time, String status, String additionalInfo) {
        try {
            String url = SERVER_URL + endpoint;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setConnectTimeout(4000);
            con.setReadTimeout(4000);

            String jsonInputString = String.format(
                    "{\"systemName\": \"%s\", \"ipAddress\": \"%s\", \"time\": \"%d\", \"status\": \"%s\", \"project\": \"%s\"}",
                    systemName, ipAddress, time, status, additionalInfo
            );

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("REST message sent successfully.");
            } else {
                System.out.println("Failed to send REST message. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
