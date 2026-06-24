package practice2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import practice1.Packet;
import practice4.ProductService;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        StoreDatabase db = new StoreDatabase();
        ProductService productService = new ProductService("jdbc:sqlite:store.db");

        BlockingQueue<byte[]> rawQueue = new ArrayBlockingQueue<>(100);
        BlockingQueue<Packet> decodedQueue = new ArrayBlockingQueue<>(100);
        BlockingQueue<Packet> responseQueue = new ArrayBlockingQueue<>(100);
        BlockingQueue<byte[]> encryptedQueue = new ArrayBlockingQueue<>(100);

        ExecutorService executorService = Executors.newFixedThreadPool(16);

        for (int i = 0; i < 2; i++)
            executorService.execute(new FakeReceiver(rawQueue));
        for (int i = 0; i < 2; i++)
            executorService.execute(new Decryptor(rawQueue, decodedQueue));
        for (int i = 0; i < 4; i++)
            executorService.execute(new Processor(decodedQueue, responseQueue, db, productService));
        for (int i = 0; i < 3; i++)
            executorService.execute(new Encryptor(responseQueue, encryptedQueue));
        for (int i = 0; i < 5; i++)
            executorService.execute(new FakeSender(encryptedQueue));

        Thread.sleep(5000);

        executorService.shutdownNow();

        if (executorService.awaitTermination(2, TimeUnit.SECONDS)) {
            System.out.println("Усі потоки успішно зупинено.");
        }

        System.out.println(
                "Залишок на складі (Шоколад Milka молочний): " + db.getQuantity("Шоколад Milka молочний"));
    }
}