package com.example.demo.repo;

import com.example.demo.entity.OrderBook;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    List<OrderBook> findBySide(String side);                       // BUY hoặc SELL
    @Query("select o from OrderBook o where o.side='BUY' and o.type='LIMIT' order by o.price desc")
    List<OrderBook> bestBid(Pageable pageable);                    // limit 1 ⇒ giá mua cao nhất

}