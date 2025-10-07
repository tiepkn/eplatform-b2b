package com.eplatform.b2b.inventory.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eplatform.b2b.inventory.client.PaymentClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import net.devh.boot.grpc.client.inject.GrpcClient;
import com.eplatform.b2b.payment.proto.PaymentServiceGrpc;
import com.eplatform.b2b.payment.proto.PingRequest;

@RestController
@RequestMapping("/api/v1")
public class InfoController {
  private final PaymentClient paymentClient;
  @GrpcClient("payment-service")
  private PaymentServiceGrpc.PaymentServiceBlockingStub paymentStub;

  public InfoController(PaymentClient paymentClient) {
    this.paymentClient = paymentClient;
  }
  @GetMapping("/info")
  public String info() {
    return "inventory-service";
  }

  @GetMapping("/health")
  public String health() {
    return "inventory-service";
  }

  @GetMapping("/payment/ping")
  public String pingPayment(@RequestParam(name = "echo", required = false, defaultValue = "ping") String echo) {
    // Calls payment-service's info endpoint via Eureka + Feign
    String paymentInfo = paymentClient.info();
    return "payment-response:" + paymentInfo + ", echo:" + echo;
  }

  @GetMapping("/payment/grpc-ping")
  public String grpcPing(@RequestParam(name = "echo", required = false, defaultValue = "ping") String echo) {
    var request = PingRequest.newBuilder().setEcho(echo).build();
    var response = paymentStub.ping(request);
    return response.getMessage();
  }
}
