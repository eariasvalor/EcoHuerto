package com.huerto.api.domain.ports.out;

import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.model.Order;

public interface WhatsAppPort {

    void sendStatusChange(String phone, String orderId, OrderStatus status);

    void sendNewOrderToAdmin(String adminPhone, Order order, String customerName);

    void sendManualNotification(String phone, String text, String mediaId);
}