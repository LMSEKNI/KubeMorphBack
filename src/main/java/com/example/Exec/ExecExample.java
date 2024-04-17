package com.example.Exec;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.kubernetes.client.Exec;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Streams;

public class ExecExample {
  public static void main(String[] args)
      throws IOException, ApiException, InterruptedException, ParseException {
    final Options options = new Options();
    options.addOption(new Option("p", "pod", true, "The name of the pod"));
    options.addOption(new Option("n", "namespace", true, "The namespace of the pod"));

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String podName = cmd.getOptionValue("p", "dweya-app-9b766c57c-592qb ");
    String namespace = cmd.getOptionValue("n", "default");
    args = cmd.getArgs();

    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);

    Exec exec = new Exec();
    boolean tty = System.console() != null;
    // final Process proc = exec.exec("default", "dweya-app-9b766c57c-592qb", new String[]
    //   {"sh", "-c", "echo foo"}, true, tty);
    final Process proc =
        exec.exec(namespace, podName, args.length == 0 ? new String[] {"sh"} : args, true, tty);

    Thread in =
        new Thread(
            new Runnable() {
              public void run() {
                try {
                  Streams.copy(System.in, proc.getOutputStream());
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              }
            });
    in.start();

    Thread out =
        new Thread(
            new Runnable() {
              public void run() {
                try {
                  Streams.copy(proc.getInputStream(), System.out);
                } catch (IOException ex) {
                  ex.printStackTrace();
                }
              }
            });
    out.start();

    proc.waitFor();

    // wait for any last output; no need to wait for input thread
    out.join();

    proc.destroy();

    System.exit(proc.exitValue());
  }
} 