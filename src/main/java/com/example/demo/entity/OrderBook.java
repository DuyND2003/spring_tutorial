package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

// OrderBook.java
@Entity
@Table(name = "orderbook")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderBook {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty
    @Column(nullable = false)  private String side;          // BUY / SELL
    @JsonProperty
    @Column(nullable = false)  private String type;          // LIMIT / MARKET
    @JsonProperty(namespace = "price")
    private Double price;
    @JsonProperty(namespace = "quantity")// null nếu MARKET
    @Column(nullable = false)  private BigDecimal quantity;
    @JsonProperty(namespace = "status")
    @Column(nullable = false)  private String status;        // NEW, FILLED …
    @JsonProperty
    @Column(name="filled_quantity") private Integer filledQuantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getFilledQuantity() {
        return filledQuantity;
    }

    public void setFilledQuantity(Integer filledQuantity) {
        this.filledQuantity = filledQuantity;
    }
}