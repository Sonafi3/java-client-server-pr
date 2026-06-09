package practice4;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProductService {
    private final Map<Integer, Product> products = new ConcurrentHashMap<>();

    public void create(Product p) {
        products.put(p.getId(), p);
    }

    public void delete(int id) {
        products.remove(id);
    }

    public void update(Product p) {
        products.put(p.getId(), p);
    }

    public Product getById(int id) {
        return products.get(id);
    }

    public List<Product> search(ProductFilter filter) {
        Stream<Product> stream = products.values().stream();

        if (filter.name != null)
            stream = stream.filter(p -> p.getName().toLowerCase().contains(filter.name.toLowerCase()));
        if (filter.category != null)
            stream = stream.filter(p -> p.getCategory().equalsIgnoreCase(filter.category));
        if (filter.minQty != null)
            stream = stream.filter(p -> p.getQuantity() >= filter.minQty);
        if (filter.maxQty != null)
            stream = stream.filter(p -> p.getQuantity() <= filter.maxQty);
        if (filter.minPrice != null)
            stream = stream.filter(p -> p.getPrice() >= filter.minPrice);
        if (filter.maxPrice != null)
            stream = stream.filter(p -> p.getPrice() <= filter.maxPrice);

        return stream.skip((long) (filter.page - 1) * filter.size)
                .limit(filter.size)
                .collect(Collectors.toList());
    }
}