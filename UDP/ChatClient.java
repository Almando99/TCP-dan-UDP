import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_IP = "127.0.0.1"; // Ganti dengan IP server jika berbeda
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            Thread receiverThread = new Thread(() -> {
                try {
                    byte[] receiveData = new byte[1024];
                    while (true) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        clientSocket.receive(receivePacket);
                        String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                        System.out.println("Pesan dari server: " + receivedMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiverThread.start();

            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan nama Anda: ");
            String clientName = scanner.nextLine();

            while (true) {
                String message = scanner.nextLine();
                message = clientName + ": " + message; // Menambahkan nama pengirim
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
