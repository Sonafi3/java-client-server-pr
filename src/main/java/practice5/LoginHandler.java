package practice5;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class LoginHandler implements HttpHandler {
    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("POST".equals(exchange.getRequestMethod())) {
            AuthModels.LoginRequest req = gson.fromJson(new InputStreamReader(exchange.getRequestBody()),
                    AuthModels.LoginRequest.class);

            if ("admin".equals(req.login) && "password".equals(req.password)) {
                String token = JwtUtil.generateToken(req.login);
                String response = gson.toJson(new AuthModels.TokenResponse(token));
                sendResponse(exchange, 200, response);
            } else {
                sendResponse(exchange, 401, "{\"error\":\"Недійсні облікові дані\"}");
            }
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Метод не дозволено\"}");
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