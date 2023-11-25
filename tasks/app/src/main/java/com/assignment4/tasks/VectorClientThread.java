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
                    clientSocket.receive(receivePacket);
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    // Assuming the response format is "message:[clock1,clock2,clock3,clock4]"
                    String[] responseMessageArray = response.split(":");
                    if (responseMessageArray.length > 1) {
                        // Extracting vector clock values from the response
                        String[] clockValues = responseMessageArray[1]
                                .replaceAll("\\p{Punct}", " ")
                                .trim()
                                .split("\\s+");

                        // Update the local vector clock with the received values
                        for (int i = 0; i < clockValues.length; i++) {
                            vcl.setVectorClock(i, Integer.parseInt(clockValues[i]));
                        }

                        // Increment local clock for receiving the message
                        vcl.tick(id);
                    }

                    System.out.println("Server: " + responseMessageArray[0] + " " + vcl.showClock());
                } catch (IOException e) {
                    System.out.println("Error receiving data: " + e.getMessage());
                    break;
                }
            }
        }

    }

