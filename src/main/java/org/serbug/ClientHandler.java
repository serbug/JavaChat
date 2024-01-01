package org.serbug;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

// Clasa care gestionează comunicarea cu un client la nivel de server
public class ClientHandler implements Runnable {
    // Socketul asociat clientului
    private final Socket clientSocket;
    // Referința către server
    private final ServerBroker server;
    // Obiectul pentru a scrie către client
    private PrintWriter output;
    // Numele clientului
    private String clientName;
    // Lista de mesaje primite și trimise de către client
    private final List<String> messages = new ArrayList<>();
    // Adresa IP a clientului
    private final String clientAddress; // Store client's address
    // Portul clientului
    private final int clientPort; // Store client's port

    // Constructorul care primește un socket, serverul asociat, adresa și portul clientului
    public ClientHandler(Socket clientSocket, ServerBroker server, String clientAddress, int clientPort) {
        // Inițializarea membrilor de clasă
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }

    // Metoda executată într-un fir de execuție separat pentru fiecare client
    @Override
    public void run() {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Setarea referinței către obiectul pentru a scrie către client
            this.output = output;

            // Citirea numelui clientului de la acesta la conectare
            this.clientName = input.readLine();
            System.out.println(clientName + " connected");

            // Informarea tuturor clienților despre noua conexiune
            server.broadcastMessage(clientName + " joined the chat", this);

            // Citirea și transmiterea mesajelor
            String message;
            while ((message = input.readLine()) != null) {
                if (message.equals("/quit")) {
                    // Trimiterea unui mesaj de deconectare tuturor celorlalți clienți
                    server.broadcastMessage(clientName + " left the chat", this);
                    System.out.println(clientName + " left the chat");
                } else if (message.startsWith("/private")) {
                    // Tratarea mesajelor private
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String recipientName = parts[1];
                        String privateMessage = parts[2];
                        server.sendPrivateMessage(clientName, recipientName, privateMessage);
                    } else {
                        sendMessage("Invalid private message format. Usage: /private recipientName messageBody");
                    }
                } else if (message.equals("/online")) {
                    // Trimiterea unei listă cu clienții online la cererea clientului
                    String onlineClients = server.getOnlineClients();
                    sendMessage(onlineClients);
                } else {
                    // Transmiterea mesajelor obișnuite la toți clienții
                    server.broadcastMessage(clientName + ": " + message, this);

                    // Adăugarea mesajului la lista de mesaje a clientului
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                    String timestampedMessage = "[" + dateFormat.format(new Date()) + "] " + message;
                    messages.add(timestampedMessage);

                    // Salvarea datelor clientului în fișierul XML
                    server.saveClientDataToXML();
                }
                // Încărcarea datelor clientului din fișierul XML
                server.loadClientDataFromXML();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Închiderea socketului la deconectare
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Eliminarea clientului din lista serverului și salvarea mesajelor înainte de închidere
            server.removeClient(this);
            server.saveClientDataToXML();
        }
    }

    // Metodă pentru trimiterea unui mesaj către client
    public void sendMessage(String message) {
        output.println(message);
    }

    // Metodă pentru obținerea numelui clientului
    public String getClientName() {
        return clientName;
    }

    // Metodă pentru obținerea adresei IP a clientului
    public String getClientAddress() {
        return clientAddress;
    }

    // Metodă pentru obținerea portului clientului
    public int getClientPort() {
        return clientPort;
    }
}
