package org.serbug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private final String serverAddress;
    private final int serverPort;
    private String clientName;
    private volatile boolean isRunning = true;
    private Socket socket;

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    private void readUsername() throws IOException {
        System.out.print("Enter your username: ");
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        clientName = consoleInput.readLine();
    }

    public void start() {
        try {
            readUsername();

            socket = new Socket(serverAddress, serverPort);

            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

                System.out.println("Connected to the server");

                output.println(clientName);

                new Thread(() -> {
                    String message;
                    try {
                        while (!socket.isClosed() && (message = input.readLine()) != null) {
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                String userInput;
                while (!socket.isClosed() && (userInput = consoleInput.readLine()) != null) {
                    output.println(userInput);
                    if ("/quit".equals(userInput)) {
                        stopClient();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopClient() {

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = false;
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8080);
        client.start();
    }
}
