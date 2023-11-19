import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class FileClientTCP {
    public static void main(String[] args) {
        try {
            // Membuat koneksi socket ke server pada localhost dan port 8889
            Socket socket = new Socket("localhost", 8889);

            // Mendapatkan jalur file dari pengguna
            Scanner scanner = new Scanner(System.in);
            System.out.print("Masukkan File yang akan dikirim: ");
            String filePath = scanner.nextLine();

            // Memanggil fungsi untuk mengirim file ke server
            sendFile(socket, filePath);

            // Menutup koneksi socket dan scanner
            socket.close();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fungsi untuk mengirim file ke server
    private static void sendFile(Socket socket, String filePath) {
    // Membuat objek File dari jalur file yang diberikan
        File file = new File(filePath);

    // Memeriksa apakah file ditemukan
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return;
        }else{
          System.out.println(" Server: file " + filePath + " telah diterima ");
      }

      try (DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
         DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

        // Mengirim nama dan ukuran file ke server
        dataOutputStream.writeUTF(file.getName());
        dataOutputStream.writeLong(file.length());

        // Menerima nama file dari server
        String receivedFileName = dataInputStream.readUTF();
        System.out.println("Received file name from server: " + receivedFileName);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Membaca konten file dan mengirimkannya ke server
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }
            // Menerima tanggapan dari server
            String serverResponse = dataInputStream.readUTF();
            System.out.println("Server response: " + serverResponse);

            // Menunggu pesan dari server mengenai status file
            String fileStatus = dataInputStream.readUTF();
            System.out.println("File status from server: " + fileStatus);

        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}
