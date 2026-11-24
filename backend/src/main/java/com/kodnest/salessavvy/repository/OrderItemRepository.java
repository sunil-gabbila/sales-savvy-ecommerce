package com.kodnest.salessavvy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kodnest.salessavvy.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderId(String orderId);
    
    
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND oi.order.status = 'SUCCESS'")
    static
    List<OrderItem> findSuccessfulOrderItemsByUserId(int userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findSuccessfulOrderItemsByUserId'");
    }

}