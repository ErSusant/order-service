package com.ebook.management.orderservice.controller;

import com.ebook.management.orderservice.dto.OrderDto;
import com.ebook.management.orderservice.dto.PaymentRequestDto;
import com.ebook.management.orderservice.dto.PaymentResponseDto;
import com.ebook.management.orderservice.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping
  public ResponseEntity<List<OrderDto>> getOrders(@RequestParam Long userId) {
    return ResponseEntity.ok(orderService.findOrdersByUser(userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.findById(id));
  }

  @PostMapping
  public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto dto) {
    return ResponseEntity.ok(orderService.createOrder(dto));
  }

  @PutMapping("/{id}/payments")
  public ResponseEntity<PaymentResponseDto> processPayment(
      @PathVariable Long id,
      @Valid @RequestBody PaymentRequestDto request) {
    return ResponseEntity.ok(orderService.processPayment(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
    orderService.cancelOrder(id);
    return ResponseEntity.noContent().build();
  }
}
