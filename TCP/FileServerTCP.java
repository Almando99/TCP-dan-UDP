import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileServerTCP {
    public static void main(String[] args) {
        try {
            // Inisialisasi server socket pada port 8889
            ServerSocket serverSocket = new ServerSocket(8889);
            System.out.println("Server berjalan..., menunggu klien.....");

            // Menunggu koneksi dari klien
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Klien baru terhubung : " + clientSocket.getRemoteSocketAddress());

                // Menangani setiap klien dalam thread terpisah
                Thread clientHandlerThread = new Thread(() -> {
                    try {
                        receiveFile(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                clientHandlerThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     // Fungsi untuk menerima file dari klien
    private static void receiveFile(Socket clientSocket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

    // Membaca nama dan ukuran file dari klien
        String fileName = dataInputStream.readUTF();
        long fileSize = dataInputStream.readLong();

        System.out.println("Menerima File: " + fileName);

    // Menentukan direktori desktop untuk menyimpan file
        Path desktopPath = Path.of(System.getProperty("user.home"), "Desktop");

    // Membuat file sementara untuk menyimpan file yang diterima
        Path tempFilePath = Files.createTempFile(desktopPath, "received_", fileName);
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFilePath.toFile())) {
            byte[] buffer = new byte[1024];
            int bytesRead;

        // Membaca konten file dari klien dan menulisnya ke file sementara
            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

        // Mengirim pesan konfirmasi ke klien
            dataOutputStream.writeUTF("File berhasil diterima: " + fileName);
            dataOutputStream.flush();

        } finally {
        // Opsional: Pindahkan file sementara ke lokasi yang diinginkan
            Path destinationPath = desktopPath.resolve("received_files").resolve(fileName);
            Files.createDirectories(destinationPath.getParent());
            Files.move(tempFilePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
