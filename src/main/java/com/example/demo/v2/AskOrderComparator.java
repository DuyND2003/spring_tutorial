package com.example.demo.v2;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Sets;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.stream.Collectors;

public class AskOrderComparator implements Comparator<Order> {

    @Override
    public int compare(Order o1, Order o2) {
        // ask order with price <= buy price
        // sorted từ bé đến lớn theo price,

        // ask1 < ask2 < ask3 < ... < askn
        // lấy top từ trái sang phải
        // ask1.price < ask2.price
        // ask1.timestamp < ask2.timestamp trong trường hợp price = nhau

        // TCN --> 3h chiều  = t1
        // TCN --> 5h chiều  = t2
        // t1 < t2
        if (o1.getPrice() > o2.getPrice()) {
            return 1; // o1 > o2
        } else if (o1.getPrice() < o2.getPrice()) {
            return -1;
        } else {
            // bằng nhau
            if (o1.getTimestamp() < o2.getTimestamp()) {
                return -1;
            } else if (o1.getTimestamp() > o2.getTimestamp()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public static void main(String[] args) {
        // tree
        // tìm các phần tử. >=x hoặc <= y
        // chứ không hỗ trợ việc in ra từ bé đến lớn

        ImmutableSortedSet<Order> a = new ImmutableSortedSet.Builder<>(new AskOrderComparator())
                .add(new Order(1000, true, 50, 1000))
                .add(new Order(1001, true, 50, 1000))
                .add(new Order(1001, true, 60, 1000))
                .add(new Order(1001, true, 40, 1000))
                .build();


        for (Order order : a) {
            System.out.println(order);
        }
    }
}
