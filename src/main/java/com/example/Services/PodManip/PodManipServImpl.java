package com.example.Services.PodManip;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

@Service

public class PodManipServImpl implements PodManipService {

    @Override
    public void createAndDeployPod(String namespace, String containerName, String image) throws IOException, ApiException {
        V1Pod pod = createPod(namespace, containerName, image);
        deployPod(pod, namespace);
    }

    @Override
    public void deletePod(String podName, String namespace) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, new V1DeleteOptions());
    }

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

    private void deployPod(V1Pod pod, String namespace) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        api.createNamespacedPod(namespace, pod, null, null, null, null);
        System.out.println("Pod Created Successufully");
    }
     public void listPodsInAllNamespaces() throws IOException, ApiException {
        CoreV1Api api = new CoreV1Api();// invokes the CoreV1Api client
        V1PodList list =
            api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        for (V1Pod item : list.getItems()) {
            System.out.println(item.getMetadata().getName());
        }
    }
}
