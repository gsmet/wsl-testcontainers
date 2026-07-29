package io.quarkus.test;

import com.github.dockerjava.core.DefaultDockerClientConfig;
import org.testcontainers.dockerclient.DockerClientProviderStrategy;

public final class Wsl2DockerClientProviderStrategy extends DockerClientProviderStrategy {

    private final DefaultDockerClientConfig dockerClientConfig;

    public Wsl2DockerClientProviderStrategy() {
        DefaultDockerClientConfig.Builder configBuilder = DefaultDockerClientConfig.createDefaultConfigBuilder();
        String dockerHost = System.getenv("DOCKER_HOST");
        if (dockerHost != null) {
            configBuilder.withDockerHost(dockerHost);
        }
        dockerClientConfig = configBuilder.build();
    }

    @Override
    public TransportConfig getTransportConfig() {
        return TransportConfig
                .builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
    }

    @Override
    protected boolean test() {
        return System.getenv("DOCKER_HOST") != null;
    }

    @Override
    protected int getPriority() {
        return 1000;
    }

    @Override
    public String getDescription() {
        return "WSL2 Docker strategy (skips socket check). Resolved dockerHost=" + dockerClientConfig.getDockerHost();
    }

    @Override
    protected boolean isPersistable() {
        return false;
    }
}
