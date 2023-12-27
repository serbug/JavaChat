package org.serbug;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class BrokerProgram {
    private static final int BROKER_PORT = 32123;

    public static void main(String[] args) throws SocketException {
        System.out.println("Broker...");
        IOperation broker = new BrokerService(BROKER_PORT);

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> readTask = CompletableFuture.runAsync(() -> {
            try {
                processMessages(broker);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, executorService);

        CompletableFuture<Void> inputTask = CompletableFuture.runAsync(() -> {
            new Scanner(System.in).nextLine();
        }, executorService);

        CompletableFuture.allOf(readTask, inputTask).join();

        executorService.shutdown();
    }

    private static void processMessages(IOperation broker) throws InterruptedException, ExecutionException {
        String message;
        while (!((message = broker.asyncRead().get()).equals("quit b"))) {
            broker.asyncWrite(message);
            broker.asyncReload();
            System.out.println(message);
        }
    }
}