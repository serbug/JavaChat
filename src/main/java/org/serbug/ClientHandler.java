package org.serbug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ServerBroker server;
    private PrintWriter output;
    private String clientName;

    public ClientHandler(Socket clientSocket, ServerBroker server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

            this.output = output;

            // Read client name
            this.clientName = input.readLine();
            System.out.println(clientName + " connected");

            // Inform all clients about the new connection
            server.broadcastMessage(clientName + " joined the chat", this);

            // Read and broadcast messages
            String message;
            while ((message = input.readLine()) != null) {
                server.broadcastMessage(message, this);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        output.println(message);
    }

    public String getClientName() {
        return clientName;
    }
}
