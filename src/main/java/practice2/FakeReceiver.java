package practice2;

import practice1.Message;
import practice1.Packet;
import practice1.PacketProcessor;

import java.util.concurrent.BlockingQueue;

public class FakeReceiver implements Receiver {
    private final BlockingQueue<byte[]> rawQueue;

    public FakeReceiver(BlockingQueue<byte[]> rawQueue) {
        this.rawQueue = rawQueue;
    }

    @Override
    public void run() {
        receiveMessage();
    }

    @Override
    public void receiveMessage() {
        try {
            int counter = 0;
            while (!Thread.currentThread().isInterrupted()) {
                String json = "{\"item\":\"Шоколад Milka молочний\", \"amount\": 10}";

                Message msg = new Message(CommandType.ADD_QTY, 1, json);
                Packet pkt = new Packet((byte) 1, msg);

                byte[] networkBytes = PacketProcessor.encode(pkt);

                rawQueue.put(networkBytes);
                System.out.println("[Receiver] Отримано пакет №" + (++counter) + " з мережі.");

                Thread.sleep(300);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}