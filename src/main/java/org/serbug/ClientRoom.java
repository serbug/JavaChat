//Receiver
package org.serbug;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Clasa care implementează funcționalitatea unui client pentru a se conecta la server
public class ClientRoom {
    // Adresa serverului la care clientul va încerca să se conecteze
    private final String serverAddress;
    // Portul serverului la care clientul va încerca să se conecteze
    private final int serverPort;
    // Numele clientului
    private String clientName;
    // Variabilă folosită pentru a marca dacă clientul rulează
    private volatile boolean isRunning = false;
    // Socketul pentru comunicarea cu serverul
    private Socket socket;

    // Constructor care primește adresa și portul serverului
    public ClientRoom(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    // Metoda care solicită utilizatorului să introducă un nume de utilizator
    private void readUsername() throws IOException {
        System.out.print("Enter your username: ");
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        clientName = consoleInput.readLine();
    }

    // Metoda care inițializează și pornește clientul
    public void start() {
        try {
            // Citeste numele de utilizator
            readUsername();

            // Se creează un socket pentru conectarea la server
            socket = new Socket(serverAddress, serverPort);

            // Fluxurile de intrare și ieșire pentru comunicarea cu serverul
            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

                // Informații de conectare la server
                System.out.println("Connected to the server");

                // Trimite numele de utilizator către server
                output.println(clientName);

                // Firul de execuție pentru citirea mesajelor de la server
                new Thread(() -> {
                    String message;
                    try {
                        while ((message = input.readLine()) != null) {
                            // Afișează mesajele primite de la server
                            System.out.println(message);
                        }
                    } catch (IOException e) {
                        // Tratează excepția dacă firul de execuție este întrerupt și clientul nu rulează
                        if (!isRunning) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                // Trimite mesaje către server
                String userInput;
                while ((userInput = consoleInput.readLine()) != null) {
                    output.println(userInput);
                    // Dacă se introduce comanda "/quit", se închide clientul și se semnalează firul de execuție de fundal să se oprească
                    if ("/quit".equals(userInput)) {
                        System.out.println(" You left the chat");
                        isRunning = true; // Semnalează firul de execuție de fundal să se oprească
                        socket.close();
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metodă pentru oprirea clientului
    public void stopClient() {
        try {
            // Închide socketul dacă nu este deja închis
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Setează starea de rulează la false
        isRunning = false;
    }

    // Metoda principală care creează un obiect Client și îl pornește
    public static void main(String[] args) {
        ClientRoom clientRoom = new ClientRoom("localhost", 8080);
        clientRoom.start();
    }
}