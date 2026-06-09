package practice2;

import practice1.Packet;
import practice1.PacketProcessor;

import java.util.concurrent.BlockingQueue;

public class Encryptor implements Runnable {
    private final BlockingQueue<Packet> responseQueue;
    private final BlockingQueue<byte[]> encryptedQueue;

    public Encryptor(BlockingQueue<Packet> responseQueue, BlockingQueue<byte[]> encryptedQueue) {
        this.responseQueue = responseQueue;
        this.encryptedQueue = encryptedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Packet packet = responseQueue.take();

                byte[] encryptedBytes = PacketProcessor.encode(packet);

                encryptedQueue.put(encryptedBytes);
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
    }
}