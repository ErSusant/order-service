package com.ebook.management.orderservice.repository;

import com.ebook.management.orderservice.domain.Order;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
  List<Order> findByUserIdAndDeletedFalse(Long userId);
  List<Order> findByUserIdAndDeletedFalseAndPlacedAtBetween(Long userId, Instant startDate, Instant endDate);
  List<Order> findByStatusAndDeletedFalse(String status);
  boolean existsByOrderNumber(String orderNumber);
}
