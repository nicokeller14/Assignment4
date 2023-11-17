package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class UdpVectorClient {

    public static void main(String[] args) throws Exception {
        System.out.println("Enter your id (1 to 3): ");
        Scanner id_input = new Scanner(System.in);
        int id = id_input.nextInt();

        // Prepare the client socket
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");

        // Initialize the buffers
        byte[] sendData;
        byte[] receiveData = new byte[1024];
        int port = 4040;
        List<String> logs = new ArrayList<>();

        // Initialize the vector clock
        VectorClock vcl = new VectorClock(4);
        vcl.setVectorClock(id, 0);

        // Ask for user input aka message to the server
        System.out.println(id + ": Enter any message:");
        Scanner input = new Scanner(System.in);

        while (true) {
            String messageBody = input.nextLine();
            // Increment clock
            vcl.tick(id);

            // Prepare the message
            String responseMessage = messageBody + ':' + vcl.showClock();

            // Check if the user wants to quit
            if (messageBody.equalsIgnoreCase("quit")) {
                clientSocket.close();
                break;
            }

            // Send the message to the server
            sendData = responseMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
            clientSocket.send(sendPacket);

            // Check if the user wants to see the history
            if (messageBody.equalsIgnoreCase("history")) {
                System.out.println("Receiving the chat history...");

                // Set timeout for receiving messages
                clientSocket.setSoTimeout(5000); // Example timeout: 5000 milliseconds

                while (true) {
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        logs.add(receivedMessage);
                    } catch (IOException e) {
                        // Timeout reached or other IO error, stop receiving
                        break;
                    }
                }

                showHistory(logs); // Prints the unsorted history
                showSortedHistory(logs); // Prints the sorted history
            } else {
                // Start the receiver thread for listening to incoming messages
                VectorClientThread client = new VectorClientThread(clientSocket, vcl, id);
                Thread receiverThread = new Thread(client);
                receiverThread.start();
            }
        }
    }

    public static void showHistory(List<String> logs) {
        // Prints the unsorted logs (history) coming from the server
        for (String message : logs) {
            System.out.println(message);
        }
    }

    public static void showSortedHistory(List<String> logs) {
        // Prints sorted logs (history) received
        System.out.println("Print sorted conversation using attached vector clocks");
        Map<String, String> logMap = new TreeMap<>(new CustomComparator());

        for (String log : logs) {
            String[] parts = log.split(":");
            logMap.put(parts[1], parts[0]); // Assuming format "message:clock"
        }

        // Printing sorted logs
        for (String key : logMap.keySet()) {
            System.out.println(logMap.get(key) + " at " + key);
        }
    }

    static class CustomComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            // Custom comparison logic for vector clocks
            // Implement this based on how you are representing your clocks as strings
            return o1.compareTo(o2);
        }
    }
}
