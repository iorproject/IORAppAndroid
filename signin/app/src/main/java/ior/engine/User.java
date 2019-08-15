package ior.engine;

import java.util.Date;

public class User {


    private String email;
    private Date registerDate;
    private String name;
    private int amountPartners;
    private int amountFollowing;
    private int amountReceipts;

    public User(String email, String name, Date registerDate) {
        this.email = email;
        this.name = name;
        this.registerDate = registerDate;
    }

    public String getEmail() {
        return email;
    }


    public Date getRegisterDate() {
        return registerDate;
    }

    public void setAmountPartners(int amountPartners) {
        this.amountPartners = amountPartners;
    }

    public void setAmountFollowing(int amountFollowing) {
        this.amountFollowing = amountFollowing;
    }

    public int getAmountPartners() {
        return amountPartners;
    }

    public int getAmountFollowing() {
        return amountFollowing;
    }

    public void setAmountReceipts(int amountReceipts) {
        this.amountReceipts = amountReceipts;
    }

    public int getAmountReceipts() {
        return amountReceipts;
    }

    public String getName() {
        return name;
    }
}
