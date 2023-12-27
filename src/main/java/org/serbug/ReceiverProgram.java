package org.serbug;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class ReceiverProgram {
    public static void main(String[] args) throws SocketException {
        System.out.println("Receiver...");

        IOperation receiver = new TransportService(new InetSocketAddress("127.0.0.1", 32123));

        CompletableFuture<Void> receiverTask = CompletableFuture.runAsync(() -> {
            // Optionally, you may choose not to send a message before entering the loop
            String initialMessage = ReceiverInfo();
            receiver.asyncWrite(initialMessage).join();

            while (true) {
                String message = receiver.asyncRead().join();
                if (message.equals("quit r")) {
                    break;
                }
                System.out.println("\n" + message);
            }
        });
        receiverTask.join();

        System.out.println("Receiver program ended.");
    }

    private static String ReceiverInfo() {
        UserData user = new UserData();
        Scanner scanner = new Scanner(System.in);

        System.out.print("Username: ");
        user.setUsername(scanner.nextLine());

        System.out.print("Your level: ");
        user.setUserLevel(Integer.parseInt(scanner.nextLine()));

        System.out.print("Your Message: ");
        user.setMessage(scanner.nextLine());

        // Avoid closing System.in for potential reuse
        // scanner.close();

        return UserData.serializeUserData(user);
    }
}
//public class ReceiverProgram {
//    public static void main(String[] args) throws SocketException {
//        System.out.println("Receiver...");
//
//        IOperation receiver = new TransportService(new InetSocketAddress("127.0.0.1", 32123));
//
//        CompletableFuture<Void> receiverTask = CompletableFuture.runAsync(() -> {
//            String message = ReceiverInfo(); // Fix this line
//            receiver.asyncWrite(message).join();
//            while (true) {
//                message = receiver.asyncRead().join();
//                if (message.equals("quit r")) {
//                    break;
//                }
//                System.out.println("\n" + message);
//            }
//        });
//        receiverTask.join();
//
//        System.out.println("Receiver program ended.");
//    }
//
//    private static String ReceiverInfo() {
//        UserData user = new UserData();
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Username: ");
//        user.setUsername(scanner.nextLine());
//
//        System.out.print("Your level : ");
//        user.setUserLevel(Integer.parseInt(scanner.nextLine()));
//
//        System.out.print("Your Message : ");
//        user.setMessage(scanner.nextLine());
//
//        scanner.close();
//
//        return UserData.serializeUserData(user);
//    }
//}
