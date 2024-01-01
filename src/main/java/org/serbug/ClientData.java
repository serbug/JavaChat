package org.serbug;


import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

// Clasa care reprezintă datele asociate unui client, inclusiv numele, adresa IP, portul și mesajele primite
class ClientData {
    // Numele clientului
    private String clientName;
    // Adresa IP a clientului
    private String ipAddress;
    // Portul clientului
    private int port;
    // Lista de mesaje primite de la client
    private List<String> messages = new ArrayList<>();

    // Metoda getter pentru numele clientului
    @XmlElement
    public String getClientName() {
        return clientName;
    }

    // Metoda setter pentru numele clientului
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    // Metoda getter pentru adresa IP a clientului
    @XmlElement
    public String getIpAddress() {
        return ipAddress;
    }

    // Metoda setter pentru adresa IP a clientului
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    // Metoda getter pentru portul clientului
    @XmlElement
    public int getPort() {
        return port;
    }

    // Metoda setter pentru portul clientului
    public void setPort(int port) {
        this.port = port;
    }

    // Metoda getter pentru lista de mesaje primite de la client
    @XmlElement
    public List<String> getMessages() {
        return messages;
    }

    // Metoda setter pentru lista de mesaje primite de la client
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    // Adăugăm această metodă pentru a obține ultimul mesaj
    public String getLastMessage() {
        if (!messages.isEmpty()) {
            return messages.get(messages.size() - 1);
        } else {
            return "No messages";
        }
    }
}