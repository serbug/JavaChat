package org.serbug;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.CompletableFuture;

public class TransportService implements IOperation {
    private final DatagramSocket transport;

    public TransportService(InetSocketAddress broker) throws SocketException {
        transport = new DatagramSocket();
        // Optionally, you can remove the following line if not needed
        // transport.connect(broker);
    }

    @Override
    public CompletableFuture<String> asyncRead() {
        return CompletableFuture.supplyAsync(() -> {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                transport.receive(packet);
                return new String(packet.getData(), 0, packet.getLength());
            } catch (SocketTimeoutException e) {
                // Handle timeout if needed
                return null;
            } catch (IOException e) {
                // Handle other IOExceptions
                e.printStackTrace();
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Void> asyncWrite(String message) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] bytes = message.getBytes();
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                transport.send(packet);
            } catch (IOException e) {
                // Handle IOException
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> asyncReload() {
        return CompletableFuture.completedFuture(null);
    }
}
//class TransportService implements IOperation {
//    private final DatagramSocket transport;
//
//    public TransportService(InetSocketAddress broker) throws SocketException {
//        transport = new DatagramSocket();
//        transport.connect(broker);
//    }
//
//    @Override
//    public CompletableFuture<String> asyncRead() {
//        return CompletableFuture.supplyAsync(() -> {
//            byte[] buffer = new byte[1024];
//            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//            try {
//                transport.receive(packet);
//                return new String(packet.getData(), 0, packet.getLength());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        });
//    }
//
//    @Override
//    public CompletableFuture<Void> asyncWrite(String message) {
//        return CompletableFuture.runAsync(() -> {
//            try {
//                byte[] bytes = message.getBytes();
//                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
//                transport.send(packet);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    @Override
//    public CompletableFuture<Void> asyncReload() {
//        return CompletableFuture.completedFuture(null);
//    }
//}