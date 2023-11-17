package com.assignment4.tasks;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class UdpLTClient {
    public static void main(String[] args) {
        Scanner id_input = null;
        Scanner input = null;

        try {
            // Ask for the process id between 1 and 3 as 0 is allocated to the server
            System.out.println("Enter your id (1 to 3): ");
            id_input = new Scanner(System.in);
            int id = id_input.nextInt();
            int port = 4040;

            // Prepare the client socket
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getByName("localhost");

            // Initialize the clock
            int startTime = 0;
            LamportTimestamp lc = new LamportTimestamp(startTime);

            // Ask for user input aka message to the server
            System.out.println("Enter any message " + id + ":");
            input = new Scanner(System.in);

            // Start the receiver thread
            LTClientThread client = new LTClientThread(clientSocket, lc);
            Thread receiverThread = new Thread(client);
            receiverThread.start();

            while (true) {
                String messageBody = input.nextLine();
                lc.tick(); // Increment clock before sending the message

                int messageTime = lc.getCurrentTimestamp();
                String responseMessage = messageBody + ':' + messageTime;

                // Check if the user wants to quit
                if (messageBody.equalsIgnoreCase("quit")) {
                    clientSocket.close();
                    break;
                }

                // Send the message to the server
                byte[] sendData = responseMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (id_input != null) {
                id_input.close();
            }
            if (input != null) {
                input.close();
            }
        }
    }
}
