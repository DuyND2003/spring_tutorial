package com.example.demo.v2;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class SimpleMatchingEngine implements MatchingEngine {
    private JedisPooled jedisPooled = new JedisPooled("localhost", 6379);


    @Override
    public void processSellOrder(Order sellOrder, ImmutableSortedSet<Order> bidOrders) {
    }

    @Override
    public void processBuyOrder(Order buyOrder, ImmutableSortedSet<Order> askOrder) {

        jedisPooled.l

        List<Order> matchOrder = getMatchOrders(buyOrder, askOrder);

        // update db // update order
        // chọn ra 4 lệnh khớp với lệnh buy

        //
    }

    @Override
    public List<Order> getMatchOrders(Order buyOrder, ImmutableSortedSet<Order> askOrders) {


        // get valid order
        ImmutableSortedSet<Order> validPriceSet = askOrders.headSet(buyOrder, true)

        // sort lại theo thứ tự , fifo & price --> không cần sort vì đã sort theo AskComparator rồi

        int currentQuantity = 0;

        List<Order> matchedOrders = new ArrayList<>();
        for (Order order : validPriceSet) {

            matchedOrders.add(order);
            currentQuantity = currentQuantity + order.getQuantity();

            if (currentQuantity > buyOrder.getQuantity()) {
                break;
            }
        }

        return matchedOrders;
    }



}
