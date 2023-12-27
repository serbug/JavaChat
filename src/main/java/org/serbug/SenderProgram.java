package org.serbug;

import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class SenderProgram {
    public static void main(String[] args) throws SocketException {
        System.out.println("Sender...");
        IOperation sender = new TransportService(new InetSocketAddress("127.0.0.1", 32123));

        UserData user = new UserData();

        auth(user);
        Thread t = new Thread(() -> {
            String message;
            do {
                message = buildMessage(user);
                sender.asyncWrite(message);
            } while (!user.getMessage().equals("quit"));
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void auth(UserData user) {
        System.out.print("Username: ");
        user.setUsername(new Scanner(System.in).nextLine());
    }

    private static String buildMessage(UserData user) {
        System.out.print("\nMin destination level: ");
        user.setUserLevel(Short.parseShort(new Scanner(System.in).nextLine()));
        System.out.println("Message: ");
        user.setMessage(new Scanner(System.in).nextLine());
        // Return the serialized message
        return UserData.serializeUserData(user);
    }
}
