package org.serbug;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;


@XmlRootElement
class UserData {
    private String username;
    private int userLevel;
    private String message;

    public UserData() {
    }

    public UserData(String username, int userLevel, String message) {
        this.username = username;
        this.userLevel = userLevel;
        this.message = message;
    }

    @XmlElement
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @XmlElement
    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static String serializeUserData(UserData user) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(UserData.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.marshal(user, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static UserData deserializeUserData(String xml) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(UserData.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xml);
            return (UserData) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }
}