package com.example.demo.controller;

import com.example.demo.entity.OrderBook;
import com.example.demo.OrderService;
import com.example.demo.entity.OrderTransaction;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/orders")
    public List<OrderBook> all(){
        List<OrderBook> result = service.getAllOrders();
        System.out.println(result);
        return result;
    }
    @GetMapping("/orders/buy")
    public List<OrderBook> buys()    { return service.getBuys(); }
    @GetMapping("/orders/sell")
    public List<OrderBook> sells()   { return service.getSells(); }
    @GetMapping("/orders/best-bid")
    public List<OrderBook> bid()     { return service.getBestBid(); }
    @GetMapping("orders/match")
    public String matchOrders(){
        service.matchOrders();
        return "Success";
    }
}