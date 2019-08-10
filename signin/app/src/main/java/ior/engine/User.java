package ior.engine;

import java.util.Date;

public class User {

    private String email;
    private Date registerDate;

    public User(String email, Date registerDate) {
        this.email = email;
        this.registerDate = registerDate;
    }

    public String getEmail() {
        return email;
    }


    public Date getRegisterDate() {
        return registerDate;
    }
}
