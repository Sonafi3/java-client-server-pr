package practice5;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import practice4.Product;
import practice4.ProductService;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

class HttpServerTest {
    private static HttpServer server;
    private static ProductService productService;
    private static final Gson gson = new Gson();
    private static final int PORT = 8081;

    @BeforeAll
    static void setUp() throws Exception {
        productService = new ProductService();
        productService.create(new Product(1, "Шоколад Milka молочний", "Солодощі", 10, 80.0));
        server = StoreServerHTTP.startServer(PORT, productService);
    }

    @AfterAll
    static void tearDown() {
        if (server != null)
            server.stop(0);
    }

    private String getJwtToken() throws Exception {
        URL url = new URL("http://localhost:" + PORT + "/login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = con.getOutputStream()) {
            os.write("{\"login\":\"admin\", \"password\":\"password\"}".getBytes("UTF-8"));
        }

        AuthModels.TokenResponse res = gson.fromJson(new InputStreamReader(con.getInputStream(), "UTF-8"),
                AuthModels.TokenResponse.class);
        return res.token;
    }

    @Test
    void testProtectedEndpointWithoutToken_ShouldReturn401() throws Exception {
        URL url = new URL("http://localhost:" + PORT + "/products/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        assertThat(con.getResponseCode()).isEqualTo(401);
    }

    @Test
    void testGetProduct_WithValidToken_ShouldReturn200() throws Exception {
        String token = getJwtToken();
        URL url = new URL("http://localhost:" + PORT + "/products/1");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + token);

        assertThat(con.getResponseCode()).isEqualTo(200);
        Product p = gson.fromJson(new InputStreamReader(con.getInputStream(), "UTF-8"), Product.class);
        assertThat(p.getName()).isEqualTo("Шоколад Milka молочний");
    }

    @Test
    void testPutProduct_UniqueName_ShouldCreate() throws Exception {
        String token = getJwtToken();
        URL url = new URL("http://localhost:" + PORT + "/products");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Bearer " + token);
        con.setRequestProperty("Content-Type", "application/json");

        Product newP = new Product(2, "Печиво Oreo", "Солодощі", 50, 45.0);
        try (OutputStream os = con.getOutputStream()) {
            os.write(gson.toJson(newP).getBytes("UTF-8"));
        }

        assertThat(con.getResponseCode()).isEqualTo(201);
        assertThat(productService.getById(2)).isNotNull();
        assertThat(productService.getById(2).getName()).isEqualTo("Печиво Oreo");
    }
}