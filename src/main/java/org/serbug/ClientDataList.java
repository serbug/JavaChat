package org.serbug;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
class ClientDataList {
    private List<ClientData> clientDataList;

    @XmlElement(name = "client")
    public List<ClientData> getClientDataList() {
        return clientDataList;
    }

    public void setClientDataList(List<ClientData> clientDataList) {
        this.clientDataList = clientDataList;
    }
}

