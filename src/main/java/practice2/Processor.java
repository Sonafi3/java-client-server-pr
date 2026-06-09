package practice2;

import practice1.Message;
import practice1.Packet;
import practice4.Product;
import practice4.ProductFilter;
import practice4.ProductService;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Processor implements Runnable {
    private final BlockingQueue<Packet> decodedQueue;
    private final BlockingQueue<Packet> responseQueue;
    private final StoreDatabase db;
    private final ProductService productService;
    private final Gson gson = new Gson();

    public Processor(BlockingQueue<Packet> decodedQueue, BlockingQueue<Packet> responseQueue,
            StoreDatabase db, ProductService productService) {
        this.decodedQueue = decodedQueue;
        this.responseQueue = responseQueue;
        this.db = db;
        this.productService = productService;
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
                } else if (requestMsg.getCType() == CommandType.SEARCH_PRODUCTS) {
                    ProductFilter filter = gson.fromJson(requestMsg.getPayload(), ProductFilter.class);
                    List<Product> results = productService.search(filter);
                    responseJson = gson.toJson(results);
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