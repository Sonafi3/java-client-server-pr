package practice5;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import practice4.Product;
import practice4.ProductService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProductHandler implements HttpHandler {
    private final ProductService productService;
    private final Gson gson = new Gson();

    public ProductHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if ("GET".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Product p = productService.getById(id);
                if (p != null)
                    sendResponse(exchange, 200, gson.toJson(p));
                else
                    sendResponse(exchange, 404, "{\"error\":\"Не знайдено\"}");

            } else if ("PUT".equals(method) && pathParts.length == 2) {
                Product newProduct = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Product.class);
                if (productService.existsByName(newProduct.getName())) {
                    sendResponse(exchange, 400, "{\"error\":\"Назва продукту вже існує\"}");
                } else {
                    productService.create(newProduct);
                    sendResponse(exchange, 201, gson.toJson(newProduct));
                }

            } else if ("POST".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Product updateData = gson.fromJson(new InputStreamReader(exchange.getRequestBody()), Product.class);

                if (productService.getById(id) == null) {
                    sendResponse(exchange, 404, "{\"error\":\"Не знайдено\"}");
                } else {
                    productService.update(updateData);
                    sendResponse(exchange, 200, gson.toJson(updateData));
                }

            } else if ("DELETE".equals(method) && pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                if (productService.getById(id) != null) {
                    productService.delete(id);
                    sendResponse(exchange, 200, "{\"status\":\"Видалено\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Не знайдено\"}");
                }
            } else {
                sendResponse(exchange, 400, "{\"error\":\"Неправильний запит\"}");
            }
        } catch (Exception e) {
            sendResponse(exchange, 500, "{\"error\":\"Внутрішня помилка сервера\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes();
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}