package practice5;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import practice4.ProductService;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class StoreServerHTTP {
    public static HttpServer startServer(int port, ProductService productService) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/login", new LoginHandler());

        HttpContext productContext = server.createContext("/products", new ProductHandler(productService));
        productContext.setAuthenticator(new JwtAuthenticator());

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("[HTTP Server] Запущено на порту " + port);
        return server;
    }

    public static void main(String[] args) throws Exception {
        ProductService ps = new ProductService("jdbc:sqlite:store.db");
        ps.create(new practice4.Product(1, "TestItem", "Test", 10, 100.0));
        startServer(8080, ps);
    }
}