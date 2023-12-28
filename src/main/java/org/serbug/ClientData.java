package org.serbug;


import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

class ClientData {
    private String clientName;
    private String ipAddress;
    private int port;
    private List<String> messages = new ArrayList<>();

    @XmlElement
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    @XmlElement
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @XmlElement
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @XmlElement
    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    // Add this method to get the last message
    public String getLastMessage() {
        if (!messages.isEmpty()) {
            return messages.get(messages.size() - 1);
        } else {
            return "No messages";
        }
    }
}
