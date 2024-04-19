package com.example.UpdateResource;

import java.io.IOException;



import io.kubernetes.client.custom.V1Patch;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.PatchUtils;


public class PatchExample {
  static String jsonPatchStr =
      "[{\"op\":\"replace\",\"path\":\"/spec/template/spec/terminationGracePeriodSeconds\",\"value\":27}]";
  static String strategicMergePatchStr =
      "{\"metadata\":{\"$deleteFromPrimitiveList/finalizers\":[\"example.com/test\"]}}";
  static String jsonDeploymentStr =
      "{\"kind\":\"Deployment\",\"apiVersion\":\"apps/v1\",\"metadata\":{\"name\":\"hello-node\",\"finalizers\":[\"example.com/test\"],\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"replicas\":1,\"selector\":{\"matchLabels\":{\"run\":\"hello-node\"}},\"template\":{\"metadata\":{\"creationTimestamp\":null,\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"terminationGracePeriodSeconds\":30,\"containers\":[{\"name\":\"hello-node\",\"image\":\"hello-node:v1\",\"ports\":[{\"containerPort\":8080,\"protocol\":\"TCP\"}],\"resources\":{}}]}},\"strategy\":{}},\"status\":{}}";
  static String applyYamlStr =
      "{\"kind\":\"Deployment\",\"apiVersion\":\"apps/v1\",\"metadata\":{\"name\":\"hello-node\",\"finalizers\":[\"example.com/test\"],\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"replicas\":1,\"selector\":{\"matchLabels\":{\"run\":\"hello-node\"}},\"template\":{\"metadata\":{\"creationTimestamp\":null,\"labels\":{\"run\":\"hello-node\"}},\"spec\":{\"terminationGracePeriodSeconds\":30,\"containers\":[{\"name\":\"hello-node\",\"image\":\"hello-node:v2\",\"ports\":[{\"containerPort\":8080,\"protocol\":\"TCP\"}],\"resources\":{}}]}},\"strategy\":{}},\"status\":{}}";

  public static void main(String[] args) throws IOException {
    try {
      AppsV1Api api = new AppsV1Api(ClientBuilder.standard().build());
      V1Deployment body =
          Configuration.getDefaultApiClient()
              .getJSON()
              .deserialize(jsonDeploymentStr, V1Deployment.class);

      // create a deployment
      V1Deployment deploy1 =
          api.createNamespacedDeployment("default", body, null, null, null, null);
      System.out.println("original deployment" + deploy1); 

      // json-patch a deployment (Useful for patching with form)
      V1Deployment deploy2 =
          PatchUtils.patch(
              V1Deployment.class,
              () ->
                  api.patchNamespacedDeploymentCall(
                      "hello-node",
                      "default",
                      new V1Patch(jsonPatchStr),
                      null,
                      null,
                      null,
                      null, // field-manager is optional
                      null,
                      null),
              V1Patch.PATCH_FORMAT_JSON_PATCH,
              api.getApiClient());
      System.out.println("json-patched deployment" + deploy2);

      // strategic-merge-patch a deployment (Strategic merge patch is a Kubernetes-specific format that allows specifying only the fields to be modified without needing to provide the entire resource state.)
      V1Deployment deploy3 =
          PatchUtils.patch(
              V1Deployment.class,
              () ->
                  api.patchNamespacedDeploymentCall(
                      "hello-node",
                      "default",
                      new V1Patch(strategicMergePatchStr),
                      null,
                      null,
                      null, // field-manager is optional
                      null,
                      null,
                      null),
              V1Patch.PATCH_FORMAT_STRATEGIC_MERGE_PATCH,
              api.getApiClient());
      System.out.println("strategic-merge-patched deployment" + deploy3);

      // apply-yaml a deployment, server side apply is available by default after kubernetes v1.16
      // or opt-in by turning on the feature gate for v1.14 or v1.15.
      // https://kubernetes.io/docs/reference/using-api/api-concepts/#server-side-apply
      V1Deployment deploy4 =
          PatchUtils.patch(
              V1Deployment.class,
              () ->
                  api.patchNamespacedDeploymentCall(
                      "hello-node",
                      "default",
                      new V1Patch(applyYamlStr),
                      null,
                      null,
                      "example-field-manager", // field-manager is required for server-side apply
                      null,
                      true,
                      null),
              V1Patch.PATCH_FORMAT_APPLY_YAML,
              api.getApiClient());
      System.out.println("application/apply-patch+yaml deployment" + deploy4);

    } catch (ApiException e) {
      System.out.println(e.getResponseBody());
      e.printStackTrace();
    }
  }
}
