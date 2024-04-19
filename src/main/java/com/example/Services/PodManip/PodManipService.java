package com.example.Services.PodManip;

import java.io.IOException;
import java.util.List;

import io.kubernetes.client.openapi.ApiException;


public interface PodManipService {
    
    List<String> listPodsInAllNamespaces() throws IOException, ApiException;
    void createAndDeployPod(String namespace, String containerName, String image) throws IOException, ApiException;
    void deletePod(String podName, String namespace) throws ApiException;
}
