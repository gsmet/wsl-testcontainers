package io.quarkus.test;

import java.net.URI;

import org.testcontainers.dockerclient.DockerClientProviderStrategy;
import org.testcontainers.dockerclient.TransportConfig;

/**
 * Custom Docker client strategy that skips the socket connectivity check
 * which fails when connecting from Windows to Docker running inside WSL2.
 *
 * @see <a href="https://github.com/testcontainers/testcontainers-java/issues/4958">testcontainers-java#4958</a>
 */
public final class Wsl2DockerClientProviderStrategy extends DockerClientProviderStrategy {

    private final URI dockerHost;

    public Wsl2DockerClientProviderStrategy() {
        String host = System.getenv("DOCKER_HOST");
        dockerHost = host != null ? URI.create(host) : null;
    }

    @Override
    public TransportConfig getTransportConfig() {
        return TransportConfig.builder()
                .dockerHost(dockerHost)
                .build();
    }

    @Override
    protected boolean test() {
        return dockerHost != null;
    }

    @Override
    protected boolean isApplicable() {
        return dockerHost != null;
    }

    @Override
    protected int getPriority() {
        return 1000;
    }

    @Override
    public String getDescription() {
        return "WSL2 Docker strategy (skips socket check). Resolved dockerHost=" + dockerHost;
    }

    @Override
    protected boolean isPersistable() {
        return false;
    }
}
