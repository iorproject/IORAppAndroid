package ior.engine;

public class AccountEngine {
    private static final AccountEngine ourInstance = new AccountEngine();

    public static AccountEngine getInstance() {
        return ourInstance;
    }

    private User user;

    private AccountEngine() {
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
