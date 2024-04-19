package com.example.Services.PodManip;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Services.KubernetesConfig.KubernetesConfigService;


import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.openapi.models.V1PodSpec;
import io.kubernetes.client.openapi.models.V1ResourceRequirements;


@Service
public class PodManipServImpl implements PodManipService {

        @Autowired
    private KubernetesConfigService kubernetesConfigService;

    @Override
    public void createAndDeployPod(String namespace, String containerName, String image) throws IOException, ApiException {
        kubernetesConfigService.configureKubernetesAccess();
        V1Pod pod = createPod(namespace, containerName, image);
        deployPod(pod, namespace);
    }

    @Override
    public void deletePod(String podName, String namespace) throws ApiException {
        
        CoreV1Api api = new CoreV1Api();
        api.deleteNamespacedPod(podName, namespace, null, null, null, null, null, null);
    }

    private V1Pod createPod(String namespace, String containerName, String image) {
        return new V1Pod()
                .metadata(new V1ObjectMeta().name(namespace))
                .spec(new V1PodSpec()
                        .containers(Collections.singletonList(
                                new V1Container()
                                        .name(containerName)
                                        .image(image)
                                        .resources(new V1ResourceRequirements()))));
    }

    private void deployPod(V1Pod pod, String namespace) throws ApiException {
        CoreV1Api api = new CoreV1Api();
        api.createNamespacedPod(namespace, pod, null, null, null, null);
        System.out.println("Pod Created Successfully");
    }

    public List<String> listPodsInAllNamespaces() throws IOException, ApiException {
        kubernetesConfigService.configureKubernetesAccess();
        CoreV1Api api = new CoreV1Api();
        V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
        List<String> podNames = new ArrayList<>();
        for (V1Pod item : list.getItems()) {
            podNames.add(item.getMetadata().getName());
        }
        return podNames;
    }
}
