package com.example.demo.repo;

import com.example.demo.entity.OrderTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {
    List<OrderTransaction> findByOrder_Id(Long orderId);
}