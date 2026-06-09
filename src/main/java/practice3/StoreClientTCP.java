package practice3;

import practice1.Message;
import practice1.Packet;
import practice1.PacketProcessor;
import practice2.CommandType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class StoreClientTCP {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 3333;

    public static void main(String[] args) {
        System.out.println("[TCP Клієнт] Запуск.");

        while (true) {
            try {
                connectAndSend();
            } catch (Exception e) {
                System.err.println("[TCP Клієнт] Помилка з'єднання: " + e.getMessage());
                System.out.println("[TCP Клієнт] Сервер недоступний. Спроба відновлення через 3 секунди.");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private static void connectAndSend() throws Exception {
        try (Socket socket = new Socket(HOST, PORT);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())) {

            System.out.println("[TCP Клієнт] Успішно підключено до сервера.");

            for (int i = 1; i <= 5; i++) {
                Message msg = new Message(CommandType.ADD_QTY, 99, "{}");
                Packet pkt = new Packet((byte) 1, msg);

                byte[] rawBytes = PacketProcessor.encode(pkt);

                out.writeInt(rawBytes.length);
                out.write(rawBytes);
                out.flush();
                System.out.println("[TCP Клієнт] Відправлено пакет " + i);

                int responseSize = in.readInt();
                byte[] responseBytes = new byte[responseSize];
                in.readFully(responseBytes);

                Packet responsePacket = PacketProcessor.decode(responseBytes);
                System.out.println("[TCP Клієнт] Відповідь сервера: " + responsePacket.getMessage().getPayload());

                Thread.sleep(2000);
            }

            throw new Exception("Імітація обриву зв'язку клієнтом.");
        }
    }
}