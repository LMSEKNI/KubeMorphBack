package com.example.YamlManip;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Yaml;

public class CreatePodYaml {

    public static void main(String[] args) throws IOException, ApiException {
        CreatePodYaml createPodYaml = new CreatePodYaml();
        createPodYaml.createAndDeployPodFromConfig();
    }

    // Method to create a pod from the provided parameters
    private V1Pod createPod(String namespace, String containerName, String image) {
        return new V1PodBuilder()
            .withNewMetadata()
            .withName(namespace)
            .endMetadata()
            .withNewSpec()
            .addNewContainer()
            .withName(containerName)
            .withImage(image)
            .withNewResources()
            .withLimits(new HashMap<>())
            .endResources()
            .endContainer()
            .endSpec()
            .build();
    }

    // Method to read a pod configuration from a YAML file
    private V1Pod readPodFromYaml(String filePath) throws IOException {
        File file = new File(filePath);
        return (V1Pod) Yaml.load(file);
    }

    // Method to deploy a pod from a YAML configuration file
    private V1Pod deployPodFromYaml(V1Pod pod) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.createNamespacedPod("default", pod, null, null, null, null);
    }

    // Method to delete a pod
    private V1Pod deletePod(String podName) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        return api.deleteNamespacedPod(podName, "default", null, null, null, null, null, new V1DeleteOptions());
    }

    // Method to encapsulate the whole process of creating and deploying a pod from configuration
    public void createAndDeployPodFromConfig() throws IOException, ApiException {
        String namespace = "apod";
        String containerName = "www";
        String image = "nginx";

        // Create a pod from parameters
        V1Pod pod = createPod(namespace, containerName, image);
        System.out.println(Yaml.dump(pod));

        // Deploy the pod from a YAML configuration file
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        V1Pod deployedPod = deployPodFromYaml(pod);
        System.out.println(deployedPod);

        // Delete the deployed pod
        V1Pod deletedPod = deletePod(deployedPod.getMetadata().getName());
        System.out.println(deletedPod);
    }
}
