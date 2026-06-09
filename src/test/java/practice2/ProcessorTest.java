package practice2;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import practice1.Message;
import practice1.Packet;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ProcessorTest {

    @Test
    void shouldProcessMessagesConcurrentlyWithoutDataLoss() throws InterruptedException {
        StoreDatabase db = new StoreDatabase();

        BlockingQueue<Packet> decodedQueue = new ArrayBlockingQueue<>(1000);
        BlockingQueue<Packet> responseQueue = new ArrayBlockingQueue<>(1000);

        ExecutorService processorPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 4; i++) {
            processorPool.execute(new Processor(decodedQueue, responseQueue, db));
        }

        int totalRequests = 100;
        ExecutorService senderPool = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        for (int i = 0; i < totalRequests; i++) {
            senderPool.execute(() -> {
                try {
                    Message msg = new Message(CommandType.ADD_QTY, 1, "{}");
                    Packet pkt = new Packet((byte) 1, msg);

                    decodedQueue.put(pkt);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        senderPool.shutdown();

        int maxWaitCycles = 50;
        while (responseQueue.size() < totalRequests && maxWaitCycles > 0) {
            Thread.sleep(100);
            maxWaitCycles--;
        }

        processorPool.shutdownNow();

        Assertions.assertThat(db.getQuantity("Шоколад Milka молочний")).isEqualTo(1200);

        Assertions.assertThat(responseQueue.size()).isEqualTo(totalRequests);
    }
}