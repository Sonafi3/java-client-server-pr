package practice2;

import practice1.Packet;
import practice1.PacketProcessor;

import java.util.concurrent.BlockingQueue;

public class Decryptor implements Runnable {
    private final BlockingQueue<byte[]> rawQueue;
    private final BlockingQueue<Packet> decodedQueue;

    public Decryptor(BlockingQueue<byte[]> rawQueue, BlockingQueue<Packet> decodedQueue) {
        this.rawQueue = rawQueue;
        this.decodedQueue = decodedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] encryptedBytes = rawQueue.take();

                Packet packet = PacketProcessor.decode(encryptedBytes);

                decodedQueue.put(packet);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}