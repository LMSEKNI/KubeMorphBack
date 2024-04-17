package com.example.GetLogs;

import java.io.IOException;
import java.io.InputStream;

import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;

public class LogsExample {
    public static void main(String[] args) throws IOException, ApiException, InterruptedException {
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api coreApi = new CoreV1Api(client);

        String targetPodName = "dweya-app-9b766c57c-592qb"; // Specify the name of the pod you want to retrieve logs from

        V1Pod targetPod = null;
        try {
            targetPod = coreApi.readNamespacedPod(targetPodName, "default", null);
        } catch (ApiException e) {
            System.out.println("Pod not found");
            System.err.println("Error: " + e.getResponseBody());
    return;
}

        if (targetPod != null) {
            PodLogs logs = new PodLogs();
            InputStream is = logs.streamNamespacedPodLog(targetPod);
            Streams.copy(is, System.out);
        } else {
            System.out.println("Pod not found");
        }
    }
}
