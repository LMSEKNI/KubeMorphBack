package com.example.Services.KubernetesConfig;

import java.io.FileReader;
import java.io.IOException;

import org.springframework.stereotype.Service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

@Service
public class KubernetesConfigServiceImpl implements KubernetesConfigService {

    public void configureKubernetesAccess() throws IOException {
        // file path to your KubeConfig
        String kubeConfigPath = System.getenv("HOME") + "/.kube/config";
        // loading the out-of-cluster config, a kubeconfig from file-system
        ApiClient client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
        // set the global default api-client to the in-cluster one from above
        Configuration.setDefaultApiClient(client);
    }
}
