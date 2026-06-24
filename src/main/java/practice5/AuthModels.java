package practice5;

public class AuthModels {
    public static class LoginRequest {
        public String login;
        public String password;
    }

    public static class TokenResponse {
        public String token;

        public TokenResponse(String token) {
            this.token = token;
        }
    }
}