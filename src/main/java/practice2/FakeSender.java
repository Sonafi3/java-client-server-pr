package practice2;

import java.util.concurrent.BlockingQueue;

public class FakeSender implements Sender {
    private final BlockingQueue<byte[]> encryptedQueue;

    public FakeSender(BlockingQueue<byte[]> encryptedQueue) {
        this.encryptedQueue = encryptedQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytesToSend = encryptedQueue.take();
                sendMessage(bytesToSend);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void sendMessage(byte[] message) {
        System.out.println("[Sender] Відправлено пакет розміром " + message.length + " байт клієнту.");
    }
}