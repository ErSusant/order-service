package com.ebook.management.orderservice.service;

import com.ebook.management.orderservice.client.PaymentClient;
import com.ebook.management.orderservice.client.UserClient;
import com.ebook.management.orderservice.domain.Order;
import com.ebook.management.orderservice.domain.OrderItem;
import com.ebook.management.orderservice.dto.OrderDto;
import com.ebook.management.orderservice.dto.PaymentRequestDto;
import com.ebook.management.orderservice.dto.PaymentResponseDto;
import com.ebook.management.orderservice.exception.ResourceNotFoundException;
import com.ebook.management.orderservice.mapper.OrderMapper;
import com.ebook.management.orderservice.repository.OrderItemRepository;
import com.ebook.management.orderservice.repository.OrderRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final OrderItemRepository orderItemRepository;
  private final PaymentClient paymentClient;
  private final UserClient userClient;

  public OrderService(OrderRepository orderRepository,
      OrderItemRepository orderItemRepository,
      PaymentClient paymentClient,
      UserClient userClient) {
    this.orderRepository = orderRepository;
    this.orderItemRepository = orderItemRepository;
    this.paymentClient = paymentClient;
    this.userClient = userClient;
  }

  @Transactional(readOnly = true)
  public List<OrderDto> findOrdersByUser(Long userId) {
    validateUserExists(userId);
    return orderRepository.findByUserIdAndDeletedFalse(userId).stream()
        .map(OrderMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Integer getOrderCountByUser(Long userId) {
    validateUserExists(userId);
    return orderRepository.findByUserIdAndDeletedFalse(userId).size();
  }

  @Transactional(readOnly = true)
  public List<OrderDto> findOrdersByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
    validateUserExists(userId);
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date are required");
    }
    if (startDate.isAfter(endDate)) {
      throw new IllegalArgumentException("Start date cannot be after end date");
    }

    Instant start = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant end = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

    return orderRepository.findByUserIdAndDeletedFalseAndPlacedAtBetween(userId, start, end).stream()
        .sorted(Comparator.comparing(Order::getPlacedAt).reversed())
        .map(OrderMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<OrderDto> findRecentOrdersByUser(Long userId) {
    validateUserExists(userId);
    return orderRepository.findByUserIdAndDeletedFalse(userId).stream()
        .sorted(Comparator.comparing(Order::getPlacedAt).reversed())
        .limit(5)
        .map(OrderMapper::toDto)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public OrderDto findById(Long id) {
    return orderRepository.findById(id)
        .filter(order -> !order.isDeleted())
        .map(OrderMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
  }

  @Transactional
  public OrderDto createOrder(OrderDto dto) {
    Order order = OrderMapper.toEntity(dto);
    order.setOrderNumber(generateOrderNumber());
    order.setPlacedAt(Instant.now());
    order.setUpdatedAt(Instant.now());
    order.setDeleted(false);
    Order savedOrder = orderRepository.save(order);
    for (OrderItem item : savedOrder.getItems()) {
      item.setOrder(savedOrder);
      orderItemRepository.save(item);
    }
    return OrderMapper.toDto(savedOrder);
  }

  @Transactional
  public void cancelOrder(Long id) {
    Order order = orderRepository.findById(id)
        .filter(o -> !o.isDeleted())
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    order.setStatus(com.ebook.management.orderservice.domain.OrderStatus.CANCELLED);
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);
  }

  @Transactional
  public PaymentResponseDto processPayment(Long orderId, PaymentRequestDto request) {
    Order order = orderRepository.findById(orderId)
        .filter(o -> !o.isDeleted())
        .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

    request.setOrderId(orderId);
    PaymentResponseDto response = paymentClient.createPayment(request);
    order.setPaymentStatus(response.getStatus());
    order.setUpdatedAt(Instant.now());
    orderRepository.save(order);
    return response;
  }

  private void validateUserExists(Long userId) {
    if (userId == null || userId <= 0) {
      throw new IllegalArgumentException("User ID is required");
    }
    if (!userClient.userExists(userId)) {
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }
  }

  private String generateOrderNumber() {
    return "ORD-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
  }
}
