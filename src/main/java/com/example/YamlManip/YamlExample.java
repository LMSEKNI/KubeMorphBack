
package com.example.YamlManip;
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


public class YamlExample {
  public static void main(String[] args) throws IOException, ApiException, ClassNotFoundException {

    V1Service svc =
        new V1ServiceBuilder()
            .withNewMetadata()
            .withName("aservice")
            .endMetadata()
            .withNewSpec()
            .withSessionAffinity("ClientIP")
            .withType("NodePort")
            .addNewPort()
            .withProtocol("TCP")
            .withName("client")
            .withPort(8008)
            .withNodePort(8080)
            .withTargetPort(new IntOrString(8080))
            .endPort()
            .endSpec()
            .build();
    System.out.println(Yaml.dump(svc));

    // Read yaml configuration file, and deploy it
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);

    // Example yaml file 
    File file = new File("test-svc.yaml");
    V1Service yamlSvc = (V1Service) Yaml.load(file);

    // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of
    // CoreV1API
    CoreV1Api api = new CoreV1Api();
    V1Service createResult = api.createNamespacedService("default", yamlSvc, null, null, null, null);
    System.out.println(createResult);

    //Delete the created testing service 
    V1Service deleteResult =
        api.deleteNamespacedService(
            yamlSvc.getMetadata().getName(),
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

