package com.ebook.management.orderservice.controller;

import com.ebook.management.orderservice.dto.ApiResponse;
import com.ebook.management.orderservice.dto.OrderDto;
import com.ebook.management.orderservice.dto.PaymentRequestDto;
import com.ebook.management.orderservice.dto.PaymentResponseDto;
import com.ebook.management.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1/orders")
@CrossOrigin("*")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<OrderDto>>> getOrders(@RequestParam("userId") Long userId) {
    try {
      List<OrderDto> orders = orderService.findOrdersByUser(userId);
      log.info("Orders fetched for user {}. Count={}", userId, orders != null ? orders.size() : 0);
      return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully", orders));
    } catch (Exception e) {
      log.error("Error fetching orders for user {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch orders: " + e.getMessage(), null));
    }
  }

  @GetMapping("/users/{userId}")
  public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByUser(@PathVariable("userId") Long userId) {
    try {
      List<OrderDto> orders = orderService.findOrdersByUser(userId);
      log.info("Orders fetched for user {} via user route. Count={}", userId, orders != null ? orders.size() : 0);
      return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully for user", orders));
    } catch (Exception e) {
      log.error("Error fetching orders for user {} via user route", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch orders for user: " + e.getMessage(), null));
    }
  }

  @GetMapping("/users/{userId}/count")
  public ResponseEntity<ApiResponse<Integer>> getOrdersCountByUser(@PathVariable("userId") Long userId) {
    try {
      Integer count = orderService.getOrderCountByUser(userId);
      log.info("Order count fetched for user {}. Count={}", userId, count);
      return ResponseEntity.ok(new ApiResponse<>(true, "Order count fetched successfully", count));
    } catch (Exception e) {
      log.error("Error fetching order count for user {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch order count: " + e.getMessage(), null));
    }
  }

  @GetMapping("/users/{userId}/between")
  public ResponseEntity<ApiResponse<List<OrderDto>>> getOrdersByUserAndDateRange(
      @PathVariable("userId") Long userId,
      @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    try {
      List<OrderDto> orders = orderService.findOrdersByUserAndDateRange(userId, startDate, endDate);
      log.info("Orders fetched for user {} between {} and {}. Count={}", userId, startDate, endDate,
          orders != null ? orders.size() : 0);
      return ResponseEntity.ok(new ApiResponse<>(true, "Orders fetched successfully for the selected date range", orders));
    } catch (Exception e) {
      log.error("Error fetching orders for user {} between {} and {}", userId, startDate, endDate, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch order date range: " + e.getMessage(), null));
    }
  }

  @GetMapping("/users/{userId}/recent")
  public ResponseEntity<ApiResponse<List<OrderDto>>> getRecentOrdersByUser(@PathVariable("userId") Long userId) {
    try {
      List<OrderDto> orders = orderService.findRecentOrdersByUser(userId);
      log.info("Recent orders fetched for user {}. Count={}", userId, orders != null ? orders.size() : 0);
      return ResponseEntity.ok(new ApiResponse<>(true, "Recent orders fetched successfully", orders));
    } catch (Exception e) {
      log.error("Error fetching recent orders for user {}", userId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch recent orders: " + e.getMessage(), null));
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<OrderDto>> getOrder(@PathVariable("id") Long id) {
    try {
      OrderDto order = orderService.findById(id);
      log.info("Order fetched for id {}", id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Order fetched successfully", order));
    } catch (Exception e) {
      log.error("Error fetching order with id {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to fetch order: " + e.getMessage(), null));
    }
  }

  @PostMapping("/create")
  public ResponseEntity<ApiResponse<OrderDto>> createOrder(@Valid @RequestBody OrderDto dto) {
    try {
      OrderDto order = orderService.createOrder(dto);
      log.info("Order created successfully with id {}", order != null ? order.getId() : null);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body(new ApiResponse<>(true, "Order created successfully", order));
    } catch (Exception e) {
      log.error("Error creating order: {}", dto, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to create order: " + e.getMessage(), null));
    }
  }

  @PutMapping("/{id}/payments")
  public ResponseEntity<ApiResponse<PaymentResponseDto>> processPayment(
      @PathVariable("id") Long id,
      @Valid @RequestBody PaymentRequestDto request) {
    try {
      PaymentResponseDto payment = orderService.processPayment(id, request);
      log.info("Payment processed successfully for order {}", id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed successfully", payment));
    } catch (Exception e) {
      log.error("Error processing payment for order {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to process payment: " + e.getMessage(), null));
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable("id") Long id) {
    try {
      orderService.cancelOrder(id);
      log.info("Order cancelled successfully with id {}", id);
      return ResponseEntity.ok(new ApiResponse<>(true, "Order cancelled successfully", null));
    } catch (Exception e) {
      log.error("Error cancelling order with id {}", id, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(new ApiResponse<>(false, "Failed to cancel order: " + e.getMessage(), null));
    }
  }
}
