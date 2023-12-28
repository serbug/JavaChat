package org.serbug;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ServerBroker {
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Map<ClientHandler, List<String>> clientMessages = new HashMap<>();



    public ServerBroker(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ServerBroker listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                String ip = Arrays.toString(serverSocket.getInetAddress().getAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, this, ip, port);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }

        // Adăugați mesajul la lista de mesaje a expeditorului
        clientMessages.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);

        saveClientDataToXML();
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


    public void loadClientDataFromXML() {
        try {
            File file = new File("client_data.xml");
            if (file.exists()) {
                JAXBContext jaxbContext = JAXBContext.newInstance(ClientDataList.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                ClientDataList clientDataList = (ClientDataList) jaxbUnmarshaller.unmarshal(file);

                if (clientDataList.getClientDataList() != null) {
                    for (ClientData clientData : clientDataList.getClientDataList()) {
                        System.out.println("Loaded client data:");
                        System.out.println("Client Name: " + clientData.getClientName());
                        System.out.println("IP Address: " + clientData.getIpAddress());
                        System.out.println("Port: " + clientData.getPort());
                        System.out.println("Messages: " + clientData.getLastMessage());
                        System.out.println("-----------------------");
                    }
                }
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void saveClientDataToXML() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ClientDataList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            List<ClientData> clientDataList = new ArrayList<>();
            for (ClientHandler clientHandler : clients) {
                ClientData clientData = new ClientData();
                clientData.setClientName(clientHandler.getClientName());
                clientData.setIpAddress(clientHandler.getClientAddress());
                clientData.setPort(clientHandler.getClientPort());
                clientData.setMessages(clientMessages.getOrDefault(clientHandler, new ArrayList<>()));
                clientDataList.add(clientData);
            }

            ClientDataList data = new ClientDataList();
            data.setClientDataList(clientDataList);

            File file = new File("client_data.xml");
            jaxbMarshaller.marshal(data, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerBroker server = new ServerBroker(8080);
        server.loadClientDataFromXML();
        Runtime.getRuntime().addShutdownHook(new Thread(server::saveClientDataToXML));
        server.start();
    }
}
