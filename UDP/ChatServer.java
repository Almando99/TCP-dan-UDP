import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 12345;
    private static Map<String, DatagramPacket> clientMap = new HashMap<>();

    public static void main(String[] args) {
        try {
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Server berjalan, menunggu koneksi...");

            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);

                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                String clientAddress = receivePacket.getAddress().getHostAddress();
                int clientPort = receivePacket.getPort();
                String senderInfo = "Dari " + clientAddress + ":" + clientPort;

                if (!clientMap.containsKey(senderInfo)) {
                    // Jika klien belum terdaftar, maka pertimbangkan pesan sebagai nama klien
                    String clientName = message.trim();
                    System.out.println("Nama klien " + senderInfo + ": " + clientName);
                    clientMap.put(senderInfo, receivePacket);
                } else {
                    // Pesan selanjutnya adalah pesan dari klien
                    System.out.println("Pesan dari " + senderInfo + ": " + message);

                    // Meneruskan pesan ke semua klien yang terhubung
                    for (Map.Entry<String, DatagramPacket> entry : clientMap.entrySet()) {
                        if (!entry.getKey().equals(senderInfo)) {
                            DatagramPacket packet = entry.getValue();
                            packet.setData(message.getBytes());
                            serverSocket.send(packet);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
