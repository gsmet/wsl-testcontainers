package io.quarkus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class WslContainerTest {

    @Test
    void startContainerAndConnect() throws Exception {
        String dockerHost = System.getenv("DOCKER_HOST");
        System.out.println("DOCKER_HOST=" + dockerHost);
        if (dockerHost != null && dockerHost.startsWith("tcp://")) {
            URI uri = URI.create(dockerHost);
            String host = uri.getHost();
            int port = uri.getPort();
            System.out.println("Testing raw socket to " + host + ":" + port + "...");
            try (Socket s = new Socket()) {
                s.connect(new InetSocketAddress(host, port), 5000);
                System.out.println("Raw socket to " + host + ":" + port + " = OK");
            } catch (Exception e) {
                System.out.println("Raw socket to " + host + ":" + port + " = FAILED: " + e.getMessage());
            }
        }

        try (GenericContainer<?> nginx = new GenericContainer<>(DockerImageName.parse("nginx:alpine"))
                .withExposedPorts(80)
                .waitingFor(Wait.forHttp("/"))) {
            nginx.start();

            String host = nginx.getHost();
            int port = nginx.getMappedPort(80);
            System.out.println("Container available at " + host + ":" + port);

            HttpURLConnection conn = (HttpURLConnection) new URL("http://" + host + ":" + port).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            assertEquals(200, conn.getResponseCode(), "nginx should return 200");
            conn.disconnect();

            assertTrue(nginx.isRunning(), "Container should be running");
        }
    }
}
