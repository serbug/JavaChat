package org.serbug;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class ServerBroker {
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();

    public ServerBroker(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ServerBroker listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
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
                client.sendMessage(sender.getClientName() + ": " + message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


    private void loadClientDataFromXML() {
        try {
            File file = new File("client_data.xml");
            if (file.exists()) {
                JAXBContext jaxbContext = JAXBContext.newInstance(ClientDataList.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                ClientDataList clientDataList = (ClientDataList) jaxbUnmarshaller.unmarshal(file);

                if (clientDataList.getClientDataList() != null) {
                    for (ClientData clientData : clientDataList.getClientDataList()) {
                        System.out.println("Loaded client data: " + clientData.getClientName());
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
