package com.ebook.management.orderservice.client;

import com.ebook.management.orderservice.dto.PaymentRequestDto;
import com.ebook.management.orderservice.dto.PaymentResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service")
public interface PaymentClient {

  @PostMapping("/api/v1/payments")
  PaymentResponseDto createPayment(@RequestBody PaymentRequestDto request);
}
