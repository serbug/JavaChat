//Broker
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

// Clasa principală a serverului care gestionează conexiunile și mesajele
public class ServerBroker {
    // Portul pe care serverul ascultă conexiuni
    private final int port;
    // Lista de obiecte ClientHandler pentru fiecare client conectat
    private final List<ClientHandler> clients = new ArrayList<>();
    // Mapa care stochează mesajele primite de la fiecare client
    private final Map<ClientHandler, List<String>> clientMessages = new HashMap<>();

    // Constructor care primește portul serverului
    public ServerBroker(int port) {
        this.port = port;
    }

    // Metodă care pornește serverul și așteaptă conexiuni de la clienți
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ServerBroker listening on port " + port);

            // Buclează pentru a aștepta noi conexiuni de la clienți
            while (true) {
                // Acceptă o conexiune de la un client
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                // Obține adresa IP a clientului sub formă de șir
                String ip = Arrays.toString(serverSocket.getInetAddress().getAddress());

                // Creează un obiect ClientHandler pentru a gestiona comunicarea cu clientul
                ClientHandler clientHandler = new ClientHandler(clientSocket, this, ip, port);
                // Adaugă ClientHandler-ul la lista de clienți
                clients.add(clientHandler);
                // Creează un fir de execuție pentru a gestiona comunicarea cu clientul
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodă care transmite un mesaj de la un client la toți ceilalți clienți
    public void broadcastMessage(String message, ClientHandler sender) {
        // Restul logicii pentru transmiterea mesajelor normale
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }

        // Adaugă mesajul în lista de mesaje a clientului
        clientMessages.computeIfAbsent(sender, k -> new ArrayList<>()).add(message);
    }

    // Metodă care elimină un client din lista de clienți
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    // Metodă care returnează o listă de clienți online sub formă de șir de caractere
    public String getOnlineClients() {
        StringBuilder onlineClients = new StringBuilder("Online clients:\n");
        for (ClientHandler client : clients) {
            onlineClients.append(client.getClientName()).append("\n");
        }
        return onlineClients.toString();
    }

    // Metodă care trimite un mesaj privat între doi clienți
    public void sendPrivateMessage(String senderName, String recipientName, String message) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(recipientName)) {
                client.sendMessage("Private message from " + senderName + ": " + message);
                return; // Ieșim din buclă după ce am găsit destinatarul
            }
        }
        // Dacă destinatarul nu a fost găsit, trimite un mesaj înapoi expeditorului
        getClientByName(senderName).sendMessage("Recipient " + recipientName + " not found or offline.");
    }

    // Metodă care obține un obiect ClientHandler după nume
    private ClientHandler getClientByName(String clientName) {
        for (ClientHandler client : clients) {
            if (client.getClientName().equals(clientName)) {
                return client;
            }
        }
        return null;
    }

    // Metodă care încarcă datele clientului dintr-un fișier XML
    public void loadClientDataFromXML() {
        try {
            File file = new File("client_data.xml");
            // Verifică dacă fișierul există
            if (file.exists()) {
                // Creează un context JAXB pentru clasa ClientDataList
                JAXBContext jaxbContext = JAXBContext.newInstance(ClientDataList.class);
                // Creează un deserializator JAXB
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                // Deserializarea obiectului ClientDataList din fișierul XML
                ClientDataList clientDataList = (ClientDataList) jaxbUnmarshaller.unmarshal(file);

                // Verifică dacă există date de client în obiectul deserializat
                if (clientDataList.getClientDataList() != null) {
                    // Parcurge lista de date ale clienților și le afișează în consolă
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

    // Metodă care salvează datele clientului într-un fișier XML
    public void saveClientDataToXML() {
        try {
            // Creează un context JAXB pentru clasa ClientDataList
            JAXBContext jaxbContext = JAXBContext.newInstance(ClientDataList.class);
            // Creează un serializator JAXB
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // Formatare pentru a adăuga rânduri noi
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

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

    // Metodă principală care pornește serverul și încarcă datele clientului la început
    public static void main(String[] args) {
        ServerBroker server = new ServerBroker(8080);
        server.loadClientDataFromXML();
        // Adaugă un hook pentru a salva datele clientului înainte de închiderea serverului
        //Runtime.getRuntime().addShutdownHook(new Thread(server::saveClientDataToXML));
        // Pornește serverul
        server.start();
    }
}
