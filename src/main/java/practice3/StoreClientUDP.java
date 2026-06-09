package practice3;

import practice1.Message;
import practice1.Packet;
import practice1.PacketProcessor;
import practice2.CommandType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class StoreClientUDP {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6666;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(2000);
            InetAddress address = InetAddress.getByName(HOST);

            Message msg = new Message(CommandType.ADD_QTY, 99, "{}");
            Packet pkt = new Packet((byte) 1, msg);
            byte[] sendData = PacketProcessor.encode(pkt);

            boolean received = false;
            int attempts = 0;

            while (!received && attempts < 3) {
                try {
                    System.out.println("[UDP Клієнт] Відправка... (спроба " + (attempts + 1) + ")");
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
                    socket.send(sendPacket);

                    byte[] buffer = new byte[2048];
                    DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
                    socket.receive(receivePacket);

                    System.out.println("[UDP Клієнт] Успішно отримано відповідь.");
                    received = true;

                } catch (SocketTimeoutException e) {
                    System.err.println("[UDP Клієнт] Пакета немає, повторюємо.");
                    attempts++;
                }
            }

            if (!received)
                System.err.println("[UDP Клієнт] Не вдалося отримати відповідь після 3 спроб.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
