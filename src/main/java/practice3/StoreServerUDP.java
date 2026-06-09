package practice3;

import practice1.Message;
import practice1.Packet;
import practice1.PacketProcessor;
import practice2.CommandType;
import practice2.StoreDatabase;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class StoreServerUDP {
    private static final int PORT = 6666;
    private static final StoreDatabase db = new StoreDatabase();

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            System.out.println("[UDP Сервер] Слухаю порт " + PORT + ".");
            byte[] buffer = new byte[2048];

            while (true) {
                DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                socket.receive(incomingPacket);

                Packet requestPacket = PacketProcessor.decode(incomingPacket.getData());

                System.out.println("[UDP Сервер] Отримано пакет ID: " + requestPacket.getPktId());
                db.changeQuantity("Шоколад Milka молочний", 10);

                Message responseMsg = new Message(CommandType.ADD_QTY, 1, "{\"status\":\"ok\"}");
                Packet responsePacket = new Packet(requestPacket.getSrc(), responseMsg);
                byte[] responseBytes = PacketProcessor.encode(responsePacket);

                DatagramPacket replyPacket = new DatagramPacket(
                        responseBytes, responseBytes.length,
                        incomingPacket.getAddress(), incomingPacket.getPort());
                socket.send(replyPacket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}