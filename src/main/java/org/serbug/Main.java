package org.serbug;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;



public class Main {

    public static void main(String[] args) throws SocketException {
        Scanner scanner = new Scanner(System.in);

        // Example for Sender
        IOperation sender = new TransportService(new InetSocketAddress("127.0.0.1", 32123));
        UserData user = new UserData();
        auth(user);

        CompletableFuture<Void> senderTask = CompletableFuture.runAsync(() -> {
            String[] message = new String[1];
            buildMessage(user, scanner, message);
            while (!user.getMessage().equals("quit")) {
                sender.asyncWrite(Arrays.toString(message)).join();
                buildMessage(user, scanner, message);
            }
        });
        senderTask.join();

        // Example for Receiver
        IOperation receiver = new TransportService(new InetSocketAddress("127.0.0.1", 32123));
        CompletableFuture<Void> receiverTask = CompletableFuture.runAsync(() -> {
            String message = receiver.asyncRead().join();
            while (!message.equals("quit r")) {
                System.out.println("\n" + message);
                message = receiver.asyncRead().join();
            }
        });
        receiverTask.join();

        // Example for Broker
        IOperation broker = new BrokerService(32123);
        CompletableFuture<Void> brokerTask = CompletableFuture.runAsync(() -> {
            String message = broker.asyncRead().join();
            while (!message.equals("quit b")) {
                broker.asyncWrite(message).join();
                broker.asyncReload().join();
                System.out.println(message);
                message = broker.asyncRead().join();
            }
        });
        brokerTask.join();

        scanner.close();
    }

    private static void auth(UserData user) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Username: ");
        user.setUsername(scanner.nextLine());
    }

    private static void buildMessage(UserData user, Scanner scanner, String[] message) {
        System.out.print("\nMin destination level: ");
        int sLevel = Integer.parseInt(scanner.nextLine());
        user.setUserLevel(sLevel);
        System.out.println("Message: ");
        user.setMessage(scanner.nextLine());
        message[0] = UserData.serializeUserData(user);
        System.out.println("\n=========================\n");
    }
}