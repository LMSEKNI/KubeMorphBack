package com.example.Services.PodManip;

import java.io.IOException;

import io.kubernetes.client.openapi.ApiException;


public interface PodManipService {
    
    void listPodsInAllNamespaces() throws IOException, ApiException;
    void createAndDeployPod(String namespace, String containerName, String image) throws IOException, ApiException;
    void deletePod(String podName, String namespace) throws ApiException;
}
