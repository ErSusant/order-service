package com.ebook.management.orderservice.mapper;

import com.ebook.management.orderservice.domain.Order;
import com.ebook.management.orderservice.domain.OrderItem;
import com.ebook.management.orderservice.dto.OrderDto;
import com.ebook.management.orderservice.dto.OrderItemDto;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

  public static OrderDto toDto(Order order) {
    if (order == null) {
      return null;
    }
    OrderDto dto = new OrderDto();
    dto.setId(order.getId());
    dto.setOrderNumber(order.getOrderNumber());
    dto.setUserId(order.getUserId());
    dto.setStatus(order.getStatus());
    dto.setTotalAmount(order.getTotalAmount());
    dto.setPaymentStatus(order.getPaymentStatus());
    dto.setShippingAddress(order.getShippingAddress());
    dto.setBillingAddress(order.getBillingAddress());
    dto.setItems(toDtoList(order.getItems()));
    return dto;
  }

  public static Order toEntity(OrderDto dto) {
    if (dto == null) {
      return null;
    }
    Order order = new Order();
    order.setId(dto.getId());
    order.setOrderNumber(dto.getOrderNumber());
    order.setUserId(dto.getUserId());
    order.setStatus(dto.getStatus());
    order.setTotalAmount(dto.getTotalAmount());
    order.setPaymentStatus(dto.getPaymentStatus());
    order.setShippingAddress(dto.getShippingAddress());
    order.setBillingAddress(dto.getBillingAddress());
    order.setItems(toEntityList(dto.getItems(), order));
    order.setPlacedAt(Instant.now());
    order.setUpdatedAt(Instant.now());
    order.setDeleted(false);
    return order;
  }

  public static OrderItemDto toDto(OrderItem item) {
    if (item == null) {
      return null;
    }
    OrderItemDto dto = new OrderItemDto();
    dto.setId(item.getId());
    dto.setBookId(item.getBookId());
    dto.setQuantity(item.getQuantity());
    dto.setUnitPrice(item.getUnitPrice());
    dto.setTotalPrice(item.getTotalPrice());
    return dto;
  }

  public static OrderItem toEntity(OrderItemDto dto, Order order) {
    if (dto == null) {
      return null;
    }
    OrderItem item = new OrderItem();
    item.setOrder(order);
    item.setBookId(dto.getBookId());
    item.setQuantity(dto.getQuantity());
    item.setUnitPrice(dto.getUnitPrice());
    item.setTotalPrice(dto.getTotalPrice());
    item.setCreatedAt(Instant.now());
    item.setUpdatedAt(Instant.now());
    return item;
  }

  private static List<OrderItemDto> toDtoList(List<OrderItem> items) {
    return items == null ? null : items.stream().map(OrderMapper::toDto).collect(Collectors.toList());
  }

  private static List<OrderItem> toEntityList(List<OrderItemDto> items, Order order) {
    return items == null ? null : items.stream().map(item -> OrderMapper.toEntity(item, order)).collect(Collectors.toList());
  }
}
