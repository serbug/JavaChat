package org.serbug;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

// Clasa care conține o listă de obiecte ClientData și este utilizată pentru serializarea/deserializarea XML
@XmlRootElement
class ClientDataList {
    // Lista de obiecte ClientData
    private List<ClientData> clientDataList;

    // Metoda getter pentru lista de obiecte ClientData
    @XmlElement(name = "client")
    public List<ClientData> getClientDataList() {
        return clientDataList;
    }

    // Metoda setter pentru lista de obiecte ClientData
    public void setClientDataList(List<ClientData> clientDataList) {
        this.clientDataList = clientDataList;
    }
}