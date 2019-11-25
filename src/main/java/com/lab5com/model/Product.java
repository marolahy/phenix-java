package com.lab5com.model;

public class Product {
    private long id;
    private double price;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    private Product(long id, double price) {
        this.id = id;
        this.price = price;
    }

    public static Product of(long id, double price){
        return new Product(id,price);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", price=" + price +
                '}';
    }
}
