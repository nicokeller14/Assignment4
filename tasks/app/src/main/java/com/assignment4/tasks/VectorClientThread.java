package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

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

                // Extracting timestamps from the received message
                String[] timestampStrings = responseMessageArray[1].replaceAll("\\p{Punct}", " ").trim().split("\\s+");

                // Parsing timestamps and updating the vector clock
                int[] receivedTimestamps = new int[timestampStrings.length];
                for (int i = 0; i < timestampStrings.length; i++) {
                    receivedTimestamps[i] = Integer.parseInt(timestampStrings[i]);
                }

                VectorClock receivedClock = new VectorClock(receivedTimestamps.length);
                for (int i = 0; i < receivedTimestamps.length; i++) {
                    receivedClock.setVectorClock(i, receivedTimestamps[i]);
                }

                // Update the clock and increment local clock (tick) for receiving the message
                vcl.updateClock(receivedClock);
                vcl.tick(id);

                System.out.println("Server: " + responseMessageArray[0] + " " + vcl.showClock());
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
