
// This class demonstrates how to use the Kubernetes Java client to watch for events (like object creation,
// deletion, modification) in Kubernetes namespaces. It sets up a watch on namespaces and prints out the
// type of event (ADDED, MODIFIED, DELETED, etc.) along with the name of the namespace involved.

package com.example.WatchNamespaces;

import java.io.IOException;

import com.google.gson.reflect.TypeToken;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;

public class WatchExample {
    public static void main(String[] args) throws IOException, ApiException{
        // Set up the Kubernetes client by obtaining the default API client configuration
        ApiClient client = Config.defaultClient();
        // Set the default API client configuration for the application
        Configuration.setDefaultApiClient(client);

        // Create an instance of CoreV1Api, which provides methods to interact with Kubernetes namespaces
        CoreV1Api api = new CoreV1Api();

        try {
            // Set up a watch on Kubernetes namespaces using the Watch utility class
            Watch<V1Namespace> watch = Watch.createWatch(
                    client, // API client
                    // Call the listNamespaceCall method of CoreV1Api to list namespaces
                    api.listNamespaceCall(
                            null, // pretty
                            false, // allowWatchBookmarks
                            null, // continue
                            null, // fieldSelector
                            null, // labelSelector
                            5, // limit
                            null, // resourceVersion
                            null, // resourceVersionMatch
                            null, // timeoutSeconds
                            Boolean.TRUE, // watch
                            null // callback
                    ),
                    // Define the type of response object to be watched
                    new TypeToken<Watch.Response<V1Namespace>>(){}.getType()
            );

            // Iterate over events (like object creation, deletion, modification) in the watched namespaces
            for (Watch.Response<V1Namespace> item : watch) {
                // Print the type of event (ADDED, MODIFIED, DELETED, etc.) and the name of the namespace involved
                System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
            }
        } catch (ApiException e) {
            // Catch any ApiException that might occur during the watch operation
            System.err.println("Exception when watching namespaces: " + e.getMessage());
        }
    }
}
