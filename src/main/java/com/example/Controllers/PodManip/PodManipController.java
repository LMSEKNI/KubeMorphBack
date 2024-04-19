package com.example.Controllers.PodManip;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Services.PodManip.PodManipService;

import io.kubernetes.client.openapi.ApiException;

@RestController
@RequestMapping("/pod")
public class PodManipController {

    @Autowired
    private PodManipService podManipService;

    @GetMapping("/createAndDeploy")
    public String createAndDeployPod(
            @RequestParam(value = "namespace") String namespace,
            @RequestParam(value = "containerName") String containerName,
            @RequestParam(value = "image") String image) {
        try {
            podManipService.createAndDeployPod(namespace, containerName, image);
            return "Pod created and deployed successfully.";
        } catch (IOException | ApiException e) {
            e.printStackTrace();
            return "Failed to create and deploy pod.";
        }
    }

    @GetMapping("/delete")
    public String deletePod(
            @RequestParam(value = "podName") String podName,
            @RequestParam(value = "namespace") String namespace) {
        try {
            podManipService.deletePod(podName, namespace);
            return "Pod deleted successfully.";
        } catch (ApiException e) {
            e.printStackTrace();
            return "Failed to delete pod.";
        }
    }

    @GetMapping("/listAll")
    public String listPodsInAllNamespaces() {
        try {
            podManipService.listPodsInAllNamespaces();
            return "Listed pods in all namespaces.";
        } catch (IOException | ApiException e) {
            e.printStackTrace();
            return "Failed to list pods in all namespaces.";
        }
    }
}
