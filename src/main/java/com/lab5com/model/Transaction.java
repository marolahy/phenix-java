package com.lab5com.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.lab5com.util.PhenixConstant.CARREFOUR_DATE_PATTERN;

public class Transaction {
    private long id;
    private Date date;
    private String shop;
    private long product;
    private long quantity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public long getProduct() {
        return product;
    }

    public void setProduct(long product) {
        this.product = product;
    }

    public long getQuantity() {
        return quantity;
    }


    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public Transaction() {

    }

    private Transaction(long id, Date date, String shop, long product, long quantity) {
        this.id = id;
        this.date = date;
        this.shop = shop;
        this.product = product;
        this.quantity = quantity;
    }

    public static Transaction of(String id, String sDate, String shop, String product, String quantity) {
        Date date = null;
        try {
            date = new SimpleDateFormat(CARREFOUR_DATE_PATTERN).parse(sDate.substring(0, 8));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return new Transaction(Long.valueOf(id), date, shop, Long.valueOf(product), Long.valueOf(quantity));
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", date=" + date +
                ", magasin='" + shop + '\'' +
                ", produit=" + product +
                ", quantite=" + quantity +
                '}';
    }
}

