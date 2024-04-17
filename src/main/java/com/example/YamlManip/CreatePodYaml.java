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
 
    //can be used to create a pod from the form
    public static void main(String[] args) throws IOException, ApiException, ClassNotFoundException {
        V1Pod pod =
            new V1PodBuilder()
                .withNewMetadata()
                .withName("apod")
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("www")
                .withImage("nginx")
                .withNewResources()
                .withLimits(new HashMap<>())
                .endResources()
                .endContainer()
                .endSpec()
                .build();
        System.out.println(Yaml.dump(pod));
    
        // Read yaml configuration file, and deploy it
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
    
        // Example yaml file 
        File file = new File("test.yaml");
        V1Pod yamlpod = (V1Pod) Yaml.load(file);
    
        // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of
        // create the pod from a yaml file
        CoreV1Api api = new CoreV1Api();
        V1Pod createResult = api.createNamespacedPod("default", yamlpod, null, null, null, null);
        System.out.println(createResult);
    
        //Delete the created testing pod 
         V1Pod deleteResult =
            api.deleteNamespacedPod(
                yamlpod.getMetadata().getName(),
                "default",
                null,
                null,
                null,
                null,
                null,
                new V1DeleteOptions());
        System.out.println(deleteResult);
      } 
    
}
