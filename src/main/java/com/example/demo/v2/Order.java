package com.example.demo.v2;

public class Order {

    private int id ;

    private long timestamp;

    private boolean side; // true is sell, false is buy

    private double price;

    private int quantity;

    public Order(long timestamp, boolean side, double price, int quantity) {
        this.timestamp = timestamp;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setSide(boolean side) {
        this.side = side;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSell() {
        return side;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "timestamp=" + timestamp +
                ", side=" + side +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
