package com.ues.edu.services;

import com.ues.edu.entities.Order;
import com.ues.edu.entities.OrderStatus;
import com.ues.edu.repositories.OrderStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Carlos Alex
 */
@Service
public class OrderStatusService {

    @Autowired
    OrderStatusRepository orderStatusRepository;

    public Order getOrderDetails(int orderId) {
        return orderStatusRepository.findOrderById(orderId);
    }

//    public void updateOrderStatus(int orderId, OrderStatus newStatus) {
//        orderStatusRepository.updateOrderStatus(orderId, newStatus);
//    }

}
