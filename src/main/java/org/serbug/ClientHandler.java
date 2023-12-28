package org.serbug;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ServerBroker server;
    private PrintWriter output;
    private String clientName;
    private final List<String> messages = new ArrayList<>();
    private final String clientAddress; // Store client's address
    private final int clientPort; // Store client's port

    public ClientHandler(Socket clientSocket, ServerBroker server, String clientAddress, int clientPort) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
    }
//    public ClientHandler(Socket clientSocket, ServerBroker server) {
//        this.clientSocket = clientSocket;
//        this.server = server;
//    }

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
                server.broadcastMessage(clientName + ": " + message, this);

                // Adăugați mesajul la lista de mesaje a clientului
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                String timestampedMessage = "[" + dateFormat.format(new Date()) + "] " + message;
                messages.add(timestampedMessage);
                server.saveClientDataToXML();

                server.loadClientDataFromXML();

            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                server.removeClient(this);
                // Salvare mesaje înainte de a închide clientul
                saveMessagesToXML();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Adăugați această metodă pentru a salva mesajele în fișierul XML
    private void saveMessagesToXML() {
        try {
            File file = new File(clientName + "_messages.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(MessageList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            MessageList messageList = new MessageList();
            messageList.setMessages(messages);

            jaxbMarshaller.marshal(messageList, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String message) {
        output.println(message);
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public int getClientPort() {
        return clientPort;
    }
}
