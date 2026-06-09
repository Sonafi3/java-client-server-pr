package practice3;

import practice1.Message;
import practice1.Packet;
import practice1.PacketProcessor;
import practice2.CommandType;
import practice2.StoreDatabase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreServerTCP {
    private static final int PORT = 3333;
    private static final StoreDatabase db = new StoreDatabase();

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[TCP Сервер] Запущено на порту " + PORT + ". Очікування клієнтів.");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[TCP Сервер] Новий клієнт підключився: " + clientSocket.getInetAddress());

                pool.execute(() -> handleClient(clientSocket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            while (true) {
                int packetSize = in.readInt();

                byte[] rawBytes = new byte[packetSize];
                in.readFully(rawBytes);

                Packet requestPacket = PacketProcessor.decode(rawBytes);
                Message requestMsg = requestPacket.getMessage();

                System.out.println("[TCP Сервер] Отримано команду від клієнта. ID пакета: " + requestPacket.getPktId());

                String responseJson = "{\"status\":\"error\"}";
                if (requestMsg.getCType() == CommandType.ADD_QTY) {
                    db.changeQuantity("Шоколад Milka молочний", 10);
                    int current = db.getQuantity("Шоколад Milka молочний");
                    responseJson = "{\"status\":\"ok\", \"message\":\"Оновлено. Залишок: " + current + "\"}";
                }

                Message responseMsg = new Message(requestMsg.getCType(), requestMsg.getBUserId(), responseJson);
                Packet responsePacket = new Packet(requestPacket.getSrc(), responseMsg);

                byte[] responseBytes = PacketProcessor.encode(responsePacket);
                out.writeInt(responseBytes.length);
                out.write(responseBytes);
                out.flush();
            }
        } catch (EOFException e) {
            System.out.println("[TCP Сервер] Клієнт відключився нормально.");
        } catch (Exception e) {
            System.out.println("[TCP Сервер] Зв'язок з клієнтом втрачено: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }
}