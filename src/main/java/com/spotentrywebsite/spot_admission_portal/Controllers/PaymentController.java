package com.spotentrywebsite.spot_admission_portal.Controllers;

import org.json.JSONObject;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    @Value("${razorpay.key.id:rzp_test_TIwYMcbmkkEfHP}")
    private String razorpayKeyId;

    @Value("${razorpay.secret.key:QIpQ5xRS7oYmHMloGVAxzsmT}")
    private String razorpaySecretKey;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> requestPayload) {
        try {
            String candidateId = (String) requestPayload.get("candidateId");

            double rawAmountInRupees = 1000.00;
            if (requestPayload.get("amount") != null) {
                rawAmountInRupees = Double.parseDouble(requestPayload.get("amount").toString());
            }

            long amountInPaise = Math.round(rawAmountInRupees * 100);

            String receipt = "receipt_spot_round";
            if (candidateId != null && !candidateId.trim().isEmpty()) {
                String cleanId = candidateId.trim().toUpperCase();
                receipt = cleanId.length() > 40 ? cleanId.substring(0, 40) : cleanId;
            }

            // Clean keys by trimming any whitespace
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId.trim(), razorpaySecretKey.trim());

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);

            Order order = razorpay.orders.create(orderRequest);
            String realRazorpayOrderId = order.get("id");

            Map<String, Object> responseOrder = new HashMap<>();
            responseOrder.put("orderId", realRazorpayOrderId);
            responseOrder.put("amount", amountInPaise);
            responseOrder.put("keyId", razorpayKeyId.trim());
            responseOrder.put("candidateId", candidateId);

            return ResponseEntity.ok(responseOrder);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(Map.of("status", "ERROR", "message", "Failed to process Razorpay order: " + e.getMessage()));
        }
    }
}