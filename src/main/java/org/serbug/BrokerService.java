package org.serbug;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;



class BrokerService implements IOperation {
    private final DatagramSocket transport;
    private final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private final HashSet<ReceiverInfo> receivers = new HashSet<>();

    public BrokerService(int brokerPort) throws SocketException {
        transport = new DatagramSocket(32123);
    }

    @Override
    public CompletableFuture<String> asyncRead() {
        return CompletableFuture.supplyAsync(() -> {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                transport.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                UserData user = UserData.deserializeUserData(message);
                processUser(user);
                return message;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> asyncWrite(String message) {
        return CompletableFuture.runAsync(() -> {
            // Implement write logic
        });
    }

    @Override
    public CompletableFuture<Void> asyncReload() {
        return CompletableFuture.runAsync(() -> {
            // Implement reload logic if needed
        });
    }

    private void processUser(UserData user) {
        if (user.getMessage().equals("subscribe")) {
            addReceiver(user);
        } else {
            messageQueue.add(UserData.serializeUserData(user));
        }
    }

    private void addReceiver(UserData user) {
        ReceiverInfo receiverInfo = new ReceiverInfo();
        receiverInfo.setName(user.getUsername());
        receiverInfo.setIpEndPoint((InetSocketAddress) transport.getRemoteSocketAddress());
        receiverInfo.setLevel(user.getUserLevel());
        receivers.add(receiverInfo);
    }
}