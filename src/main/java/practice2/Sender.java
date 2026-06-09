package practice2;

public interface Sender extends Runnable {
    void sendMessage(byte[] message);
}