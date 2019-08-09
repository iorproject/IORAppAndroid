package ior.engine;

import java.util.Date;

public class Receipt {

    private final String email;
    private final String company;
    private final Date creationDate;
    private final float totalPrice;
    private final eCurrency currency;
    private final String fileName;
    private final String fileUrl;

    public Receipt(String email, String company, Date date,
                   float totalPrice, eCurrency currency,
                   String fileName, String fileUrl) {


        this.email = email;
        this.company = company;
        this.creationDate = date;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.fileName = fileName;
        this.fileUrl = fileUrl;

    }
}
