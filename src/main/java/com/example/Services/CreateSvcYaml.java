package com.example.Services;

import java.io.File;
import java.io.IOException;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

public class CreateSvcYaml {

    public static void main(String[] args) throws IOException, ApiException {
        CreateSvcYaml createSvcYaml = new CreateSvcYaml();
        createSvcYaml.createAndDeployServiceFromConfig();
    }

    // Create a service from the provided parameters
    private V1Service createService(String serviceName, String sessionAffinity, String type, String portName,
            int portNumber, int nodePort, int targetPort) {
        return new V1ServiceBuilder().withNewMetadata().withName(serviceName).endMetadata().withNewSpec()
                .withSessionAffinity(sessionAffinity).withType(type).addNewPort().withProtocol("TCP").withName(portName)
                .withPort(portNumber).withNodePort(nodePort).withTargetPort(new IntOrString(targetPort)).endPort().endSpec()
                .build();
    }

    // Read a service configuration from a YAML file
    private V1Service readServiceFromYaml(String filePath) throws IOException {
        File file = new File(filePath);
        return (V1Service) Yaml.load(file);
    }

    // Deploy a service from a YAML configuration file
    private V1Service deployServiceFromYaml(V1Service service) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.createNamespacedService("default", service, null, null, null, null);
    }

    // Delete a service
    private V1Service deleteService(String serviceName) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.deleteNamespacedService(serviceName, "default", null, null, null, null, null,
                new V1DeleteOptions());
    }

    // Encapsulate the whole process of creating and deploying a service from configuration
    public void createAndDeployServiceFromConfig() throws IOException, ApiException {
        // Create a service from parameters
        V1Service service = createService("aservice", "ClientIP", "NodePort", "client", 8008, 8080, 8080);
        System.out.println(Yaml.dump(service));

        // Deploy the service from a YAML configuration file
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        V1Service deployedService = deployServiceFromYaml(service);
        System.out.println(deployedService);

        // Delete the deployed service
        V1Service deletedService = deleteService(deployedService.getMetadata().getName());
        System.out.println(deletedService);
    }
}
