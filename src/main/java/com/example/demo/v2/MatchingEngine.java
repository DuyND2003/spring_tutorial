package com.example.demo.v2;

import com.google.common.collect.ImmutableSortedSet;

import java.util.List;

public interface MatchingEngine {


    void processBuyOrder(Order buyOrder, ImmutableSortedSet<Order> askOrders);

    void processSellOrder(Order sellOrder, ImmutableSortedSet<Order> bidOrders);

}
