package practice2;

import practice1.Message;
import practice1.Packet;

import java.util.concurrent.BlockingQueue;

public class Processor implements Runnable {
    private final BlockingQueue<Packet> decodedQueue;
    private final BlockingQueue<Packet> responseQueue;
    private final StoreDatabase db;

    public Processor(BlockingQueue<Packet> decodedQueue, BlockingQueue<Packet> responseQueue, StoreDatabase db) {
        this.decodedQueue = decodedQueue;
        this.responseQueue = responseQueue;
        this.db = db;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Packet requestPacket = decodedQueue.take();
                Message requestMsg = requestPacket.getMessage();

                String responseJson = "{\"status\":\"error\"}";

                if (requestMsg.getCType() == CommandType.ADD_QTY) {
                    db.changeQuantity("Шоколад Milka молочний", 10);
                    int currentQty = db.getQuantity("Шоколад Milka молочний");
                    responseJson = "{\"status\":\"ok\", \"message\":\"Успіх. Поточний залишок: " + currentQty + "\"}";
                }

                Message responseMsg = new Message(requestMsg.getCType(), requestMsg.getBUserId(), responseJson);
                Packet responsePacket = new Packet(requestPacket.getSrc(), responseMsg);

                responseQueue.put(responsePacket);
                System.out.println("[Processor] Оброблено пакет ID: " + requestPacket.getPktId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}