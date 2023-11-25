package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static com.assignment4.tasks.UdpServer.isInteger;

public class ServerThread implements Runnable {

    // prepare response message for the client
    byte[] sendData = new byte[1024];
    private final DatagramSocket socket;
    private final DatagramPacket receivePacket;
    private final InetAddress ClientIP;
    private final int clientPort;
    LamportTimestamp cl;
    VectorClock vcl;
    ArrayList<String> activities;
    public ServerThread(DatagramSocket serverSocket, DatagramPacket receivePacket, InetAddress clientIP, int clientPort, LamportTimestamp cl, VectorClock vcl, ArrayList<String> activities) {
        this.socket = serverSocket;
        this.receivePacket = receivePacket;
        this.ClientIP = clientIP;
        this.clientPort = clientPort;
        this.cl = cl;
        this.vcl = vcl;
        this.activities=activities;
    }

    @Override
    public void run() {
        try {
            String responseMessage = null;
            // receive data from the clients
            String messageFromClient = (new String(receivePacket.getData(), 0, receivePacket.getLength())).trim();

            // process the received message
            String[] receivedMessage = messageFromClient.split(":");
            String receivedMessageBody = receivedMessage[0];

            if (!messageFromClient.isEmpty()) {
                // check if the user wants to see the history and send the messages continuously
                if (receivedMessageBody.equalsIgnoreCase("history")) {
                    socket.setBroadcast(true);
                    System.out.println("Sending the chat history...");
                    Collections.shuffle(activities);
                    for (String activity : activities) {
                        try {
                            responseMessage = activity;
                            sendData = responseMessage.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ClientIP, clientPort);
                            socket.send(sendPacket);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                } else {
                    // check the type of clock implementation and respond accordingly
                    if (isInteger(receivedMessage[1])) {
                        // responses for clients using Lamport timestamp
                        cl.updateClock(Integer.parseInt(receivedMessage[1]));
                        System.out.println("Client: " + receivedMessageBody+ ":" + cl.getCurrentTimestamp());
                        String messageBody = receivedMessageBody.toUpperCase(); // simple echo response from the server-- uppercase
                        long randomDelay = (long) (Math.random() * 4000) + 1000;
                        TimeUnit.MILLISECONDS.sleep(randomDelay);
                        int messageTime = cl.getCurrentTimestamp();
                        responseMessage = messageBody+ ':' +messageTime;
                        if (!responseMessage.isEmpty()) {
                            cl.tick();
                        }
                    } else {
                        // responses for clients using Vector Clock
                        HashMap<Integer, Integer> messageTime = new HashMap<>();
                        String[] receivedValues = receivedMessage[1].replaceAll("\\p{Punct}", " ").trim().split("\\s+");
                        int processId = Integer.parseInt(receivedValues[0]);
                        int time = Integer.parseInt(receivedValues[1]);
                        VectorClock clientClock = new VectorClock(4);
                        clientClock.setVectorClock(processId, time);

                        // update and increment the clock
                        vcl.updateClock(clientClock);
                        vcl.tick(0);

                        messageTime.put(0, vcl.getCurrentTimestamp(0));
                        String messageBody = receivedMessageBody.toUpperCase(); // simple echo response from the server-- uppercase
                        Message msg = new Message(messageBody, messageTime);
                        responseMessage = msg.content + ':' + msg.messageTime;
                        if (!responseMessage.isEmpty()) {
                            vcl.tick(0);
                        }
                        System.out.println("Client: " + receivedMessageBody + " " + vcl.showClock());
                        activities.add(receivedMessageBody + ":" + vcl.showClock());
                    }

                    // send the respective response to the clients
                    sendData = responseMessage.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ClientIP, clientPort);
                    System.out.println("Replying ...");
                    socket.send(sendPacket);
                }
                if (responseMessage == null) {
                    System.out.println("Empty message, can't be sent!");
                    throw new AssertionError();
                }
            }
        } catch(IOException | InterruptedException e){
            throw new RuntimeException(e);
        }

    }
}