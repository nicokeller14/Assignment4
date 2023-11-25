package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VectorClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    private final VectorClock vcl;
    private final byte[] receiveData = new byte[1024];
    private final int id;

    public VectorClientThread(DatagramSocket clientSocket, VectorClock vcl, int id) {
        this.clientSocket = clientSocket;
        this.vcl = vcl;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket); // Receive the packet from the server

                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String[] responseMessageArray = response.split(":");

                // Check the response format
                if (responseMessageArray.length < 2) {
                    System.err.println("Error: Response received in an unexpected format.");
                    continue;
                }

                // Separate the actual message from its associated vector clock
                String actualResponseMessage = responseMessageArray[0];
                String vectorClockString = responseMessageArray[1].trim();

                // Use regex to identify vector clock elements
                Pattern vectorClockPattern = Pattern.compile("(\\d+)=(\\d+)");
                Matcher vectorClockMatcher = vectorClockPattern.matcher(vectorClockString);

                // Process each entry in the vector clock
                while (vectorClockMatcher.find()) {
                    int clockIndex = Integer.parseInt(vectorClockMatcher.group(1));
                    int clockValue = Integer.parseInt(vectorClockMatcher.group(2));
                    if (clockIndex >= 0 && clockIndex < vcl.getSize()) {
                        vcl.setVectorClock(clockIndex, clockValue);
                    }
                }
                vcl.tick(id);

                System.out.println("Server: " + responseMessageArray[0] + " " + vcl.showClock());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
