package com.example.demo.handler;

import java.util.HashSet;
import java.util.Set;

public class MatchingEngineHandler {

    private MatchingEngine engine;

    private Set<Order> buyOrders;

    private Set<Order> sellOrders;

    private Set<Order> askOrders;

    private Set<Order> bidOrders;

    private MatchingEngine matchingEngine;

    public MatchingEngineHandler() {
        buyOrders = new HashSet<>();
        sellOrders = new HashSet<>();
        askOrders = new HashSet<>();
        bidOrders = new HashSet<>();

        matchingEngine = new SimpleMatchingEngine();
    }

    public void process() {
        // init order, add sample orders
//        buyOrders.add();
//        buyOrders.add();
//
        // execute
        matchingEngine.exceute(new Order());
    }
}
