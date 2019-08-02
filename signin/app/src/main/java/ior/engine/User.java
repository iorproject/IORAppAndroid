package ior.engine;

import java.util.Date;

public class User {

    private String email;
    private String name;
    private Date registerDate;

    public User(String email, String name, Date registerDate) {
        this.email = email;
        this.name = name;
        this.registerDate = registerDate;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public Date getRegisterDate() {
        return registerDate;
    }
}
