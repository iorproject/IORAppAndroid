package ior.engine;

import java.util.Date;

public class Receipt {

    private final String email;
    private final String company;
    private final String receiptNumber;
    private final Date creationDate;
    private final float totalPrice;
    private final eCurrency currency;
    private final String fileName;
    private final String attachmentUrl;

    public Receipt(String email, String company, String number, Date date,
                   float totalPrice, eCurrency currency,
                   String fileName, String attachmentUrl) {


        this.email = email;
        this.company = company;
        this.receiptNumber = number;
        this.creationDate = date;
        this.totalPrice = totalPrice;
        this.currency = currency;
        this.fileName = fileName;
        this.attachmentUrl = attachmentUrl;

    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public eCurrency getCurrency() {
        return currency;
    }

    public String getFileName() {
        return fileName;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }
}
