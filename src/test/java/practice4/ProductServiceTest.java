package practice4;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceTest {

    @Test
    void testCrudOperations() {
        ProductService service = new ProductService();
        Product product = new Product(1, "Milka", "Chocolate", 10, 80.0);

        service.create(product);
        assertThat(service.getById(1)).isNotNull();
        assertThat(service.getById(1).getName()).isEqualTo("Milka");

        Product updatedProduct = new Product(1, "Milka Caramel", "Chocolate", 15, 90.0);
        service.update(updatedProduct);
        assertThat(service.getById(1).getName()).isEqualTo("Milka Caramel");
        assertThat(service.getById(1).getPrice()).isEqualTo(90.0);

        service.delete(1);
        assertThat(service.getById(1)).isNull();
    }

    @Test
    void testDynamicFiltering() {
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

    @Test
    void testPagination() {
        ProductService service = new ProductService();
        for (int i = 1; i <= 5; i++) {
            service.create(new Product(i, "Item " + i, "Category", 10, 50.0));
        }

        ProductFilter filter = new ProductFilter();
        filter.page = 1;
        filter.size = 2;

        List<Product> page1 = service.search(filter);
        assertThat(page1).hasSize(2);

        filter.page = 3;
        List<Product> page3 = service.search(filter);
        assertThat(page3).hasSize(1);
    }
}