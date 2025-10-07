package com.eplatform.b2b.payment.grpc;

import com.eplatform.b2b.payment.proto.PaymentServiceGrpc;
import com.eplatform.b2b.payment.proto.PingRequest;
import com.eplatform.b2b.payment.proto.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class PaymentGrpcService extends PaymentServiceGrpc.PaymentServiceImplBase {

  @Override
  public void ping(PingRequest request, StreamObserver<PingResponse> responseObserver) {
    String echo = request.getEcho().isBlank() ? "ping" : request.getEcho();
    PingResponse response = PingResponse.newBuilder()
        .setMessage("payment-service:" + echo)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
