package practice4;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {
    @Test
    void testDynamicFilteringAndPagination() {
        ProductService service = new ProductService();
        service.create(new Product(1, "Milka", "Chocolate", 10, 80.0));
        service.create(new Product(2, "Roshen", "Chocolate", 20, 40.0));
        service.create(new Product(3, "Apple", "Fruit", 5, 60.0));

        ProductFilter filter = new ProductFilter();
        filter.category = "Chocolate";
        filter.minPrice = 60.0;

        List<Product> result = service.search(filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Milka");
    }
}