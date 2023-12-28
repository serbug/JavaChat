package org.serbug;

import javax.xml.bind.annotation.XmlElement;

class ClientData {
    private String clientName;

    private String ipAddress;  // adaugat pentru exemplu

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
}
