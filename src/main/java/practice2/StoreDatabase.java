package practice2;

import java.util.concurrent.ConcurrentHashMap;

public class StoreDatabase {
    private final ConcurrentHashMap<String, Integer> inventory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> prices = new ConcurrentHashMap<>();

    public StoreDatabase() {
        inventory.put("Шоколад Milka молочний", 200);
        prices.put("Шоколад Milka молочний", 80);
    }

    public int getQuantity(String item) {
        return inventory.getOrDefault(item, 0);
    }

    public void changeQuantity(String item, int amount) {
        inventory.merge(item, amount, Integer::sum);
    }

    public void setPrice(String item, int price) {
        prices.put(item, price);
    }

}