package com.xcoder.transaction.handler;

import java.time.LocalDateTime;

import com.xcoder.transaction.entity.AuditLog;
import com.xcoder.transaction.entity.Order;
import com.xcoder.transaction.exception.PaymentValidatorException;
import com.xcoder.transaction.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentValidatorHandler {
    private final AuditLogRepository auditLogRepository;

    //@Transactional(propagation = Propagation.MANDATORY)
    @Transactional(propagation = Propagation.NESTED)
    public void validatePayment(Order order) {
        // Assume payment processing happens here
        boolean paymentSuccessful = false;

        // If payment is successful, we log the payment failure in the mandatory transaction
        if (!paymentSuccessful) {
            AuditLog paymentFailureLog = new AuditLog();
            paymentFailureLog.setOrderId((long) order.getId());
            paymentFailureLog.setAction("Payment Failed for Order");
            paymentFailureLog.setTimestamp(LocalDateTime.now());

            if (order.getTotalPrice() > 1000) {
                throw new PaymentValidatorException("Error is payment validator");
            }

            // save the payment failure log
            auditLogRepository.save(paymentFailureLog);
        }
    }
}
