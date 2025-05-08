package com.example.demo.v2;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;

public class SimpleMatchingEngine implements MatchingEngine {

    @Override
    public void processBuyOrder(Order buyOrder, ImmutableSortedSet<Order> askOrders) {

//        SortedSet<Order> validPriceOrders  = Sets.newTreeSet((Comparator<Order>) (o1, o2) -> {
//
//        });
//
//        validPriceOrders.headSet()


    }

    @Override
    public void processSellOrder(Order sellOrder, ImmutableSortedSet<Order> bidOrders) {

    }
}
