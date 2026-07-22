package io.quarkus.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

public class WslContainerTest {

    @Test
    void startContainerAndConnect() throws Exception {
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
