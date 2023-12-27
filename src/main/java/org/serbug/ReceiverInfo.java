package org.serbug;

import java.net.InetSocketAddress;

public class ReceiverInfo {
    private String name;
    private InetSocketAddress ipEndPoint;
    private int level;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetSocketAddress getIpEndPoint() {
        return ipEndPoint;
    }

    public void setIpEndPoint(InetSocketAddress ipEndPoint) {
        this.ipEndPoint = ipEndPoint;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
