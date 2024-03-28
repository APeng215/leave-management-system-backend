package mypackage.util;

public class LoginResponse {
    boolean loginSucceed;
    String username;

    public LoginResponse() {
    }

    public LoginResponse(boolean loginSucceed, String username) {
        this.loginSucceed = loginSucceed;
        this.username = username;
    }
}
