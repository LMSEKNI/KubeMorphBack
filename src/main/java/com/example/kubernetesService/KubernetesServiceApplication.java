
// This class demonstrates how to use the Kubernetes Java client to access a Kubernetes cluster
// from outside using the kubeconfig file. It loads the kubeconfig file from the file system,
// sets up an out-of-cluster config, creates an API client, sets it as the default client, and
// then uses the CoreV1Api client to list all pods in all namespaces.

package com.example.kubernetesService;
import java.io.FileReader;
import java.io.IOException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;



public class KubernetesServiceApplication {
    public static void main(String[] args) throws IOException, ApiException {
     // file path to your KubeConfig

    String kubeConfigPath = System.getenv("HOME") + "/.kube/config";

    // loading the out-of-cluster config, a kubeconfig from file-system
    ApiClient client =
        ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();

    // set the global default api-client to the in-cluster one from above
    Configuration.setDefaultApiClient(client);

    // the CoreV1Api loads default api-client from global configuration.
    CoreV1Api api = new CoreV1Api();

    // invokes the CoreV1Api client
    V1PodList list =
        api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null, null);
    for (V1Pod item : list.getItems()) {
      System.out.println(item.getMetadata().getName());
    }
  }
}



