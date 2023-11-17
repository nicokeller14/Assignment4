package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class LTClientThread implements Runnable {

    private final DatagramSocket clientSocket;
    LamportTimestamp lc;

    byte[] receiveData = new byte[1024];

    public LTClientThread(DatagramSocket clientSocket, LamportTimestamp lc) {
        this.clientSocket = clientSocket;
        this.lc = lc;
    }

    @Override
    public void run() {
        /*
         * write your code to continuously receive the response from the server and update the clock value with the received value
         */

        /*
         * write your code to parse the response. Remember the response you receive is in message:timestamp format.
         * response.split(":");
         * update clock every time the client receives a message
         */
            while (true) { // Loop to continuously receive messages
                try {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    clientSocket.receive(receivePacket); // Receive a packet
                    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());

                    System.out.println("Server:" + response + ":" + lc.getCurrentTimestamp());

                    // Split the response and update the clock
                    lc.updateClock(Integer.parseInt(response.split(":")[1]));
                    System.out.println("Updated Time:" + lc.getCurrentTimestamp());
                } catch (IOException e) {
                    e.printStackTrace();
                    break; // Exit the loop in case of an IO exception
                }
            }
        }

    }

